package com.example.mycontactlist_;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class ContactDataSource {

    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource(Context context){
        dbHelper = new ContactDBHelper(context);
    }
    public void open()throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close(){
        dbHelper.close();
    }
    public boolean insertContact(Contact c){
        boolean didSucceed = false;
        try{
            ContentValues initialValues = new ContentValues();

            initialValues.put("contactname",c.getContactName());
            initialValues.put("streetaddress",c.getStreetAddress());
            initialValues.put("city",c.getCity());
            initialValues.put("state",c.getState());
            initialValues.put("zipcode",c.getZipCode());
            initialValues.put("phonenumber",c.getPhoneNumber());
            initialValues.put("cellnumber",c.getCellNumber());
            initialValues.put("email",c.getEMail());
            initialValues.put("birthday",String.valueOf (c.getBirthday().getTimeInMillis()));

            // Listing 8.16 Save picture to Database
            if (c.getPicture() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                c.getPicture().compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] photo = baos.toByteArray();
                initialValues.put("contactphoto", photo);
            }

            didSucceed = database.insert("contact",null, initialValues) >0;
        }
        catch (Exception e){
            //do nothing - will return false if there is an exception
        }
        return didSucceed;
    }
    public int getLastContactID() {
        int lastId;
        try {
            String query = "select MAX(_id) from contact";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        } catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }
    public ArrayList<String> getContactName(){
        ArrayList<String> contactNames = new ArrayList<>();
        try{
            String query = "Select contact name from contact";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                contactNames.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            contactNames = new ArrayList<String>();
        }
        return contactNames;
    }

    //Listing 6.6 get Contacts Method
    public ArrayList<Contact> getContacts(String sortField,String sortOrder) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        try {
            String query = "SELECT * FROM contact ORDER BY" + sortField + " " + sortOrder;
            Cursor cursor = database.rawQuery(query, null);

            Contact newContact;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                newContact = new Contact();
                newContact.setContactID(cursor.getInt(0));
                newContact.setContactName(cursor.getString(1));
                newContact.setStreetAddress(cursor.getString(2));
                newContact.setCity(cursor.getString(3));
                newContact.setState(cursor.getString(4));
                newContact.setZipCode(cursor.getString(5));
                newContact.setPhoneNumber(cursor.getString(6));
                newContact.setCellNumber(cursor.getString(7));
                newContact.setEMail(cursor.getString(8));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
                newContact.setBirthday(calendar);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            contacts = new ArrayList<Contact>();
        }
        return contacts;
    }
    //Listing 6.9 Get Specific contacts Method
    public Contact getSpecificContact(int contactId){
        Contact contact = new Contact();
        String query = "SELECT * FROM contact WHERE _id =" + contactId;
        // is "contactID"  in line 114 a typo?
        //attempted to replace w/ contactId
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()){
            contact.setContactID(cursor.getInt(0));
            contact.setContactName(cursor.getString(1));
            contact.setStreetAddress(cursor.getString(2));
            contact.setCity(cursor.getString(3));
            contact.setState(cursor.getString(4));
            contact.setZipCode(cursor.getString(5));
            contact.setPhoneNumber(cursor.getString(6));
            contact.setCellNumber(cursor.getString(7));
            contact.setEMail(cursor.getString(8));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
            contact.setBirthday(calendar);

            cursor.close();
        }
        return contact;
    }
    //Listing 6.13 delete contact Method
    public Boolean deleteContact(int contactId) {
        //made method public instead of private to fix error in ContactAdapter
        boolean didDelete = false;
        try {
            didDelete = database.delete("contact", "_id=" + contactId, null) > 0;
        } catch (Exception e) {
            //Do nothing -return value already set to false
        }
        return didDelete;
    }



    public boolean  updateContact (Contact c) {
        boolean didSucceed = false;
        try{
            Long rowId = (long) c.getContactID();
            ContentValues updateValues = new ContentValues();

            updateValues.put("contactname",c.getContactName());
            updateValues.put("streetaddress",c.getStreetAddress());
            updateValues.put("city",c.getCity());
            updateValues.put("state",c.getState());
            updateValues.put("zipcode",c.getZipCode());
            updateValues.put("phonenumber",c.getPhoneNumber());
            updateValues.put("cellnumber",c.getCellNumber());
            updateValues.put("email",c.getEMail());
            updateValues.put("birthday",String.valueOf (c.getBirthday().getTimeInMillis()));

            // Listing 8.16
            if (c.getPicture() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                c.getPicture().compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] photo = baos.toByteArray();
                updateValues.put("contactphoto", photo);
            }

            didSucceed = database.update("contact",updateValues,"_id=" + rowId, null) >0;
        }
        catch (Exception e){
            //do nothing - will return false if there is an exception
        }
        return didSucceed;
    }
}
