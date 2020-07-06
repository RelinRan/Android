package com.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人助手
 */
public class ContactHelper {

    public static final int REQUEST_CODE = 852;

    /**
     * 上下文对象
     */
    private Context context;

    /**
     * Json对象
     */
    private StringBuffer json;

    /**
     * 联系人列表对象
     */
    private List<Contact> contacts;

    /**
     * 对象
     */
    public static ContactHelper helper;

    /**
     * 正在获取标识
     */
    public static final int WHAT_CONTACT_OBTAINING = 0;

    /**
     * 获取完毕标识
     */
    public static final int WHAT_CONTACT_OBTAINED = 1;

    private ContactHelper(Context context) {
        this.context = context;
    }

    public static ContactHelper with(Context context) {
        if (helper == null) {
            synchronized (ContactHelper.class) {
                if (helper == null) {
                    helper = new ContactHelper(context);
                }
            }
        }
        return helper;
    }

    public static class Contact {

        /**
         * 联系人ID
         */
        private String id;
        /**
         * 联系人名字
         */
        private String name;
        /**
         * 联系人电话
         */
        private String phone;
        /**
         * 备注
         */
        private String note;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    /**
     * 获取联系人列表
     *
     * @return
     */
    public ContactHelper obtaion() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                json = new StringBuffer("[");
                contacts = new ArrayList<Contact>();
                int progress = 0;
                Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                int sum = cursor.getCount();
                while (cursor.moveToNext()) {
                    json.append("{");
                    //新建一个联系人实例
                    Contact contract = new Contact();
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    contactId = contactId == null ? "" : contactId;
                    contract.id = contactId;
                    json.append("\"id" + "\":" + "\"" + contactId + "\"");
                    json.append(",");
                    //获取联系人姓名
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    name = name == null ? "" : name;
                    contract.name = name.replace(" ", "");
                    json.append("\"name" + "\":" + "\"" + name + "\"");
                    json.append(",");
                    //获取联系人电话号码
                    Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                    while (phoneCursor.moveToNext()) {
                        String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phone = phone == null ? "" : phone;
                        phone = phone.replace("-", "");
                        phone = phone.replace(" ", "");
                        phone = phone.replace("+", "");
                        contract.phone = phone;
                        json.append("\"phone" + "\":" + "\"" + phone + "\"");
                        json.append(",");
                    }
                    //获取联系人备注信息
                    Cursor noteCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                            new String[]{ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Nickname.NAME}, ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "'",
                            new String[]{contactId}, null);
                    if (noteCursor.moveToFirst()) {
                        do {
                            String note = noteCursor.getString(noteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                            note = note == null ? "" : note;
                            contract.note = note;
                            json.append("\"note" + "\":" + "\"" + note + "\"");
                        } while (noteCursor.moveToNext());
                    }
                    json.append("}");
                    json.append(",");
                    progress++;
                    contacts.add(contract);
                    Message message = handler.obtainMessage();
                    message.obj = contacts;
                    message.arg1 = sum;
                    message.arg2 = progress;
                    message.what = WHAT_CONTACT_OBTAINING;
                    handler.sendMessage(message);
                    phoneCursor.close();
                    noteCursor.close();
                }
                if (json.toString().contains(",")) {
                    json = json.deleteCharAt(json.lastIndexOf(","));
                }
                json.append("]");
                cursor.close();
                Message message = handler.obtainMessage();
                message.obj = contacts;
                message.arg1 = sum;
                message.arg2 = progress;
                message.what = WHAT_CONTACT_OBTAINED;
                handler.sendMessage(message);
            }
        }.start();
        return this;
    }

    /**
     * 联系人json
     *
     * @return
     */
    public String json() {
        if (json == null) {
            return "";
        }
        return json.toString();
    }

    /**
     * 联系人List
     *
     * @return
     */
    public List<Contact> list() {
        return contacts;
    }

    /**
     * 联系人监听
     */
    public OnContactObtainListener onContactObtainListener;

    /**
     * 设置联系人获取监听
     *
     * @param onContactObtainListener
     * @return
     */
    public ContactHelper OnContactObtainListener(OnContactObtainListener onContactObtainListener) {
        this.onContactObtainListener = onContactObtainListener;
        return this;
    }

    /**
     * 联系人获取监听
     */
    public interface OnContactObtainListener {


        /**
         * 联系人监听
         *
         * @param max      最大数量
         * @param progress
         */
        void onContactObtainProgress(int max, int progress);


        /**
         * 联系人获取结果
         *
         * @param contacts 俩呢喜人
         */
        void onContactObtainResult(ArrayList<Contact> contacts);

    }

    /**
     * 联系人处理
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<Contact> contacts = (ArrayList<Contact>) msg.obj;
            int max = msg.arg1;
            int progress = msg.arg2;
            if (msg.what == WHAT_CONTACT_OBTAINING) {
                if (onContactObtainListener != null) {
                    onContactObtainListener.onContactObtainProgress(max, progress);
                }
            }
            if (msg.what == WHAT_CONTACT_OBTAINED) {
                if (onContactObtainListener != null) {
                    onContactObtainListener.onContactObtainResult(contacts);
                }
            }
        }
    };

    /**
     * 选择联系人
     */
    public void select(Activity activity) {
        select(activity, REQUEST_CODE);
    }

    /**
     * 选择联系人
     *
     * @param fragment
     */
    public void select(Fragment fragment) {
        select(fragment, REQUEST_CODE);
    }


    /**
     * 选择联系人
     */
    public void select(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 选择联系人
     *
     * @param fragment
     */
    public void select(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        fragment.startActivityForResult(intent, requestCode);
    }


    /**
     * 处理onActivityResult
     *
     * @param requestCode 请求码
     * @param resultCode  结果代码
     * @param data        请求数据
     */
    public Contact onActivityResult(int requestCode, int resultCode, Intent data) {
        Contact contact = null;
        if (requestCode == requestCode && resultCode == Activity.RESULT_OK) {
            if (data.getData() == null) {
                return null;
            }
            Cursor cursor = context.getContentResolver().query(data.getData(), null, null, null, null);
            contact = new Contact();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                contact.setName(name);
                contact.setId(id);
                if (hasPhone.equalsIgnoreCase("1")) {
                    hasPhone = "true";
                } else {
                    hasPhone = "false";
                }
                if (Boolean.parseBoolean(hasPhone)) {
                    Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNumber.replace(" ", "");
                        phoneNumber = phoneNumber.replace("-", "");
                        phoneNumber = phoneNumber.replace("+", "");
                        contact.setPhone(phoneNumber);
                    }
                    phones.close();
                    //Note信息获取
                    Cursor noteCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                            new String[]{ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Nickname.NAME}, ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "'",
                            new String[]{id}, null);
                    if (noteCursor.moveToFirst()) {
                        do {
                            String note = noteCursor.getString(noteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                            contact.note = note;
                        } while (noteCursor.moveToNext());
                    }
                    noteCursor.close();
                } else {
                    contact.setPhone("");
                    contact.setNote("");
                }
            }
            cursor.close();
        }
        return contact;
    }


}
