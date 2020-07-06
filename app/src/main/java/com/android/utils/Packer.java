package com.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

/**
 * Created by Relin
 * on 2018-11-22.<br/>
 * 加壳工具,通过调用{@link Packer#addShell(File, File)}<br/>
 * 方法进行对apk进行加壳处理，处理完毕之后会生成一个dex<br/>
 * 文件，把这个dex文件放在员dex的原来的壳项目中，然后配置<br/>
 * meta为name=APPLICATION_CLASS_NAME,value配置为自己项目<br/>
 * 的类名，然后进行签名就可以了。<br/>
 * add case tool, by calling the {@link Packer# addShell (File, the File)} < br / >
 * method to add case the apk, after processed generates a dex < br / >
 * file, put the dex file in dex original shell project, then configure < br / >
 * for meta name = APPLICATION_CLASS_NAME, value configuration for your project < br / >
 * the name of the class, then it is ok to sign. < br / >
 */
public class Packer {

    /**
     * 加壳
     *
     * @param apk apk源文件
     * @param dex dex壳文件
     * @return
     */
    public static File addShell(File apk, File dex) {
        //以二进制形式读出apk，并进行加密处理//对源Apk进行加密操作
        byte[] apkArray = readFileBytes(apk);
        //以二进制形式读出dex
        byte[] dexArray = readFileBytes(dex);
        int apkLen = apkArray.length;
        int dexLen = dexArray.length;
        //多出4字节是存放长度的。
        int totalLen = apkLen + dexLen + 4;
        // 申请了新的长度
        byte[] newDex = new byte[totalLen];
        //添加解壳代码,先拷贝dex内容
        System.arraycopy(dexArray, 0, newDex, 0, dexLen);
        //添加加密后的解壳数据,再在dex内容后面拷贝apk的内容
        System.arraycopy(apkArray, 0, newDex, dexLen, apkLen);
        //添加解壳数据长度,最后4为长度
        System.arraycopy(intToByte(dexLen), 0, newDex, totalLen - 4, 4);

        //修改DEX file size文件头
        fixFileSizeHeader(newDex);
        //修改DEX SHA1 文件头
        fixSHA1Header(newDex);
        //修改DEX CheckSum文件头
        fixCheckSumHeader(newDex);

        File file = new File(dex.getParent() + File.separator + "classes_shell.dex");
        FileOutputStream localFileOutputStream = null;
        try {
            localFileOutputStream = new FileOutputStream(file);
            localFileOutputStream.write(newDex);
            localFileOutputStream.flush();
            localFileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 修改dex头，CheckSum 校验码
     *
     * @param dexBytes
     */
    private static void fixCheckSumHeader(byte[] dexBytes) {
        Adler32 adler = new Adler32();
        adler.update(dexBytes, 12, dexBytes.length - 12);//从12到文件末尾计算校验码
        long value = adler.getValue();
        int va = (int) value;
        byte[] newcs = intToByte(va);
        //高位在前，低位在前掉个个
        byte[] recs = new byte[4];
        for (int i = 0; i < 4; i++) {
            recs[i] = newcs[newcs.length - 1 - i];
            System.out.println(Integer.toHexString(newcs[i]));
        }
        System.arraycopy(recs, 0, dexBytes, 8, 4);//效验码赋值（8-11）
        System.out.println(Long.toHexString(value));
        System.out.println();
    }


    /**
     * int 转byte[]
     *
     * @param number
     * @return
     */
    public static byte[] intToByte(int number) {
        byte[] b = new byte[4];
        for (int i = 3; i >= 0; i--) {
            b[i] = (byte) (number % 256);
            number >>= 8;
        }
        return b;
    }

    /**
     * 修改dex头 sha1值
     *
     * @param dexBytes
     * @throws NoSuchAlgorithmException
     */
    private static void fixSHA1Header(byte[] dexBytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(dexBytes, 32, dexBytes.length - 32);//从32为到结束计算sha--1
        byte[] newdt = md.digest();
        System.arraycopy(newdt, 0, dexBytes, 12, 20);//修改sha-1值（12-31）
        //输出sha-1值，可有可无
        String hexstr = "";
        for (int i = 0; i < newdt.length; i++) {
            hexstr += Integer.toString((newdt[i] & 0xff) + 0x100, 16)
                    .substring(1);
        }
        System.out.println(hexstr);
    }

    /**
     * 修改dex头 file_size值
     *
     * @param dexBytes
     */
    private static void fixFileSizeHeader(byte[] dexBytes) {
        //新文件长度
        byte[] newfs = intToByte(dexBytes.length);
        System.out.println(Integer.toHexString(dexBytes.length));
        byte[] refs = new byte[4];
        //高位在前，低位在前掉个个
        for (int i = 0; i < 4; i++) {
            refs[i] = newfs[newfs.length - 1 - i];
            System.out.println(Integer.toHexString(newfs[i]));
        }
        System.arraycopy(refs, 0, dexBytes, 32, 4);//修改（32-35）
    }


    /**
     * 以二进制读出文件内容
     *
     * @param file
     * @return
     * @throws IOException
     */
    private static byte[] readFileBytes(File file) {
        byte[] arrayOfByte = new byte[1024];
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            int i = 0;
            try {
                i = fis.read(arrayOfByte);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i != -1) {
                localByteArrayOutputStream.write(arrayOfByte, 0, i);
            } else {
                return localByteArrayOutputStream.toByteArray();
            }
        }
    }

}
