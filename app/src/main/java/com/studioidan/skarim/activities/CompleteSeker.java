package com.studioidan.skarim.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.studioidan.skarim.App;
import com.studioidan.skarim.R;
import com.studioidan.skarim.data.CPM;
import com.studioidan.skarim.data.DataStore;
import com.studioidan.skarim.data.Keys;
import com.studioidan.skarim.entities.LocationTrackItem;
import com.studioidan.skarim.entities.Stop;
import com.studioidan.skarim.entities.Survey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class CompleteSeker extends ActionBarActivity implements OnClickListener {

    EditText et_busNumber, et_numberOFSits, et_busKind, etFree1, etFree2;
    Survey survey;
    String dataFormat = "dd/MM/yyyy kk:mm:ss";
    String dataFormatFile = "dd.MM.yyyy kk:mm";
    String surviorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_seker);
        survey = App.current_Survey;
        init();
    }

    private void init() {
        //reference
        et_busNumber = (EditText) findViewById(R.id.et_complete_seker_bus_number);
        et_numberOFSits = (EditText) findViewById(R.id.et_complete_seker_number_of_sits);
        et_busKind = (EditText) findViewById(R.id.et_complete_seker_bus_kind);
        etFree1 = (EditText) findViewById(R.id.et_complete_seker_free_text1);
        etFree2 = (EditText) findViewById(R.id.et_complete_seker_free_text2);

        surviorId = CPM.getString(Keys.surviorId, "error", CompleteSeker.this);
    }


    @Override
    public void onClick(View v) {
        String busNumber = et_busNumber.getText().toString().trim();
        String numOfSits = et_numberOFSits.getText().toString().trim();
        String busKind = et_busKind.getText().toString().trim();
        String free1 = etFree1.getText().toString().trim();
        String free2 = etFree2.getText().toString().trim();

        App.current_Survey.busNumber = busNumber;
        App.current_Survey.BusKind = busKind;
        App.current_Survey.NumOfSits = numOfSits;
        App.current_Survey.freeField1 = free1;
        App.current_Survey.freeField2 = free2;


        File f = CreateExelFile();
        //sendEmail(f);
    }

    private void sendEmail(File f) {

        Session session = createSessionObject();

        try {
            //Message message = createMessage("studioidan@gmail.com", "sub", "Body", session,f);
            Message message = createMessage("ROM.transport.survey@gmail.com", f.getName(), "survey file", session, f);
            //Message message = createMessage("studioidan@gmail.com", "survey file", "survey file", session,f);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private Message createMessage(String email, String subject, String messageBody, Session session, File file) throws MessagingException, UnsupportedEncodingException {

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("ROM.transport.survey@gmail.com", "Survey"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        //message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);


        String htmlBody = "Body here";
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte fileContent[] = new byte[(int) file.length()];

        try {
            fin.read(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Multipart mp = new MimeMultipart();

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html");
        mp.addBodyPart(htmlPart);

        MimeBodyPart attachment = new MimeBodyPart();
        attachment.setFileName(file.getName());
        attachment.setContent(fileContent, "application/xls");
        mp.addBodyPart(attachment);

        message.setContent(mp);

        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ROM.transport.survey@gmail.com", "romtransport1");
                //return new PasswordAuthentication("studioidan@gmail.com", "gibson12");
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CompleteSeker.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            // we done with this survey
            CPM.putBoolean(Keys.isInMiddle, false, CompleteSeker.this);

            showSuccessDialog();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public File CreateExelFile() {
        String str_path = Environment.getExternalStorageDirectory().toString();
        File file;
        file = new File(str_path, survey.tripid_plan + "_" + surviorId + "_" + DateFormat.format(dataFormatFile, new Date()) + ".xls");

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setEncoding("UTF-8");
        //wbSettings.setLocale(new Locale("he", "HE"));
        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);

            //First Sheet
            WritableSheet sheet = workbook.createSheet("Report", 0);
            WritableSheet sheetGPS = workbook.createSheet("GPS data", 1);

            // Create titles
            int i = 0;
            sheet.addCell(new Label(i++, 0, "tripid_plan"));
            sheet.addCell(new Label(i++, 0, "surveyorid"));
            sheet.addCell(new Label(i++, 0, "tablet_id"));
            sheet.addCell(new Label(i++, 0, "date_start"));
            sheet.addCell(new Label(i++, 0, "date_end"));
            sheet.addCell(new Label(i++, 0, "timeDoorOpen"));
            sheet.addCell(new Label(i++, 0, "timeDoorClose"));
            sheet.addCell(new Label(i++, 0, "stopId"));
            sheet.addCell(new Label(i++, 0, "on"));
            sheet.addCell(new Label(i++, 0, "off"));
            sheet.addCell(new Label(i++, 0, "continue_by_survior"));
            sheet.addCell(new Label(i++, 0, "latitude"));
            sheet.addCell(new Label(i++, 0, "longitude"));

            sheet.addCell(new Label(i++, 0, "Bus number"));
            sheet.addCell(new Label(i++, 0, "Number of sits"));
            sheet.addCell(new Label(i++, 0, "Bus kind"));
            sheet.addCell(new Label(i++, 0, "Free text 1"));
            sheet.addCell(new Label(i++, 0, "Free text 2"));


            i = 1; // ���� �����
            int j = 0; // ���� ������
            for (Stop stop : survey.stops) {
                // watch details
                j = 0;
                sheet.addCell(new Label(j++, i, survey.tripid_plan));
                sheet.addCell(new Label(j++, i, surviorId));
                sheet.addCell(new Label(j++, i, survey.tablet_id));
                sheet.addCell(new Label(j++, i, "" + DateFormat.format(dataFormat, survey.date_start)));
                sheet.addCell(new Label(j++, i, "" + DateFormat.format(dataFormat, survey.date_end)));
                sheet.addCell(new Label(j++, i, "" + DateFormat.format(dataFormat, stop.timeDoorOpen)));
                sheet.addCell(new Label(j++, i, "" + DateFormat.format(dataFormat, stop.timeDoorClose)));
                sheet.addCell(new Label(j++, i, "" + stop.stopId));
                sheet.addCell(new Label(j++, i, "" + stop.On));
                sheet.addCell(new Label(j++, i, "" + stop.Off));
                sheet.addCell(new Label(j++, i, "" + stop.continue_by_survior));
                sheet.addCell(new Label(j++, i, "" + stop.lat));
                sheet.addCell(new Label(j++, i, "" + stop.lon));

                sheet.addCell(new Label(j++, i, "" + et_busNumber.getText().toString().trim()));
                sheet.addCell(new Label(j++, i, "" + et_numberOFSits.getText().toString().trim()));
                sheet.addCell(new Label(j++, i, "" + et_busKind.getText().toString().trim()));
                sheet.addCell(new Label(j++, i, "" + etFree1.getText().toString().trim()));
                sheet.addCell(new Label(j++, i, "" + etFree2.getText().toString().trim()));

                i += 1;
            }

            ///   Create GPS sheet   ///

            // Create titles
            i = 0;
            sheetGPS.addCell(new Label(i++, 0, "tripid_plan"));
            sheetGPS.addCell(new Label(i++, 0, "surveyorid"));
            sheetGPS.addCell(new Label(i++, 0, "date"));
            sheetGPS.addCell(new Label(i++, 0, "doorMode"));
            sheetGPS.addCell(new Label(i++, 0, "Speed (km/h)"));
            sheetGPS.addCell(new Label(i++, 0, "latitude"));
            sheetGPS.addCell(new Label(i++, 0, "longitude"));
            sheetGPS.addCell(new Label(i++, 0, "degrees"));
            sheetGPS.addCell(new Label(i++, 0, "direction"));

            i = 1; // row
            j = 0; //clumn
            for (LocationTrackItem locationTrackItem : survey.locationChain) {
                j = 0;
                sheetGPS.addCell(new Label(j++, i, "" + locationTrackItem.tripId));
                sheetGPS.addCell(new Label(j++, i, "" + surviorId));
                sheetGPS.addCell(new Label(j++, i, "" + DateFormat.format(dataFormat, locationTrackItem.date)));
                sheetGPS.addCell(new Label(j++, i, "" + locationTrackItem.DoorMode));
                sheetGPS.addCell(new Label(j++, i, "" + locationTrackItem.speed));
                sheetGPS.addCell(new Label(j++, i, "" + locationTrackItem.lat));
                sheetGPS.addCell(new Label(j++, i, "" + locationTrackItem.lon));
                sheetGPS.addCell(new Label(j++, i, "" + locationTrackItem.deg));
                sheetGPS.addCell(new Label(j++, i, "" + locationTrackItem.direction));
                i++;
            }

            workbook.write();
            workbook.close();

            //showSuccessDialog();
        } catch (WriteException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        sendEmail(file);

        return file;

    }

    private void showSuccessDialog() {
        final Dialog dialog = new Dialog(CompleteSeker.this);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setTitle("Finished");

        // set the custom dialog components - text, image and button
        Button ok = (Button) dialog.findViewById(R.id.btn_dialog_success_ok);

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                // delete this survey
                DataStore.getInstance().getSurveys().remove(survey);
                DataStore.getInstance().save();
                App.current_Survey = null;


                CompleteSeker.this.finish();
                MainActivity.start(CompleteSeker.this, true);

            }
        });
        dialog.show();
    }
}
