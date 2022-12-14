package com.frankensterzenbach.paulfranken.myapplication;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.frankensterzenbach.paulfranken.myapplication.R.id.text102;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text108;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text16;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text26;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text31;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text37;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text4;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text41;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text46;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text56;
import static com.frankensterzenbach.paulfranken.myapplication.R.id.text65;

public class MainActivity extends AppCompatActivity implements OnClickListener,View.OnLongClickListener {




    //---------------------------------------------ArrayList f??r verschidene Informationen---------------------------------------------
    public static ArrayList<TextView2> alleStunden = new ArrayList<TextView2>();
    //Alle TextViews (Stunden) werden hier geschpeichert
    public static ArrayList<String> speichern_laden = new ArrayList<String>();
    //ArrayList die zum Speichern und zum Laden verwendet werden muss
    public static ArrayList<StundeVplan> vertreungsplan_daten=new ArrayList<StundeVplan>();
    //Arraylist, in der alle Daten die vom Vertretungsplan kommen, geschpeichert werden.
    //---------------------------------------------ArrayList f??r verschidene Informationen---------------------------------------------



    //---------------------------------------------Integer, Strings, Menu, Booleans----------------------------------------------------
    public boolean heute=false,morgen=false,montag=false;
    //Booleans um den Toast zu erstellen, der besagt ob der Vertretungsplan verf??gbar ist (MyTask)
    private Menu mymenu;
    //Menu muss gespeichert werden um zu erm??glichen, das die Animation beim Aktualisieren im Menu stattfinden kann.
    public static TextView2 bearbeiten;
    //TextView zur aktuellen, an der Bearbeitende Stunde
    public static int bearbeiteni;
    //Id f??r TextView das zu bearbeiten ist
    public String kurs, kursid,text,raum2;
    //Public Strings: kurs=Lk oder Gk, kursid=Kursnummer,text=Selbstlerenen oder Vertretung, raum2=Bei RaumVertretung (MyTask2)
    public static String fach,stunde,test3 = "??", klasse="Q2";
    private File pdfFile;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    public static String c="";
    //Static Strings:fach=welches fach,stunde=welche stunde,test3=nicht anr??hren,(MyTask2)klasse=welche klasse man ist(Standard=Q2)
    //---------------------------------------------Integer, Strings, Menu, Booleans----------------------------------------------------


    private static final String URL_REGISTER_DEVICE = "https://gammy-bowls.000webhostapp.com/RegisterDevice.php";
    //----------------------------------------------Layout, Werbung und Context speichern----------------------------------------------
    public SwipeRefreshLayout l;
    //Das Main Layout wird unter l geschpeichert
    public AdView adView;
    //Die Werbungsanzeige wird geschpeichert
    public static Context context;
    //Der Context f??r die gesamte Application wird geschpeichert. Zum Beispiel f??r Toasts
    //----------------------------------------------Layout, Werbung und Context speichern----------------------------------------------

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        context=getApplicationContext();
sendTokenToServer();

        //Speichern des Layouts und festlegen der Toolbar, Speichern des ApplicationContexts










        //--------------------------------------------------------Swipe Layout---------------------------------------------------------

        l=(SwipeRefreshLayout)findViewById(R.id.swipe);

        l.setColorSchemeResources(R.color.f1,R.color.f4,R.color.f7);//Farben festlegen

        l.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {

                l.setRefreshing(true);

                (new Handler()).postDelayed(new Runnable() {

                    @Override

                    public void run() {

                        aktualisieren();

                        //Der Vertretungsplan wird erneut ??berpr??ft und abgeglichen

                    }

                },3000);



            }

        });

        //Das Swipe Layout(Main Layout wird in der Variable l geschpeichert. Die Farben werden festgelegt, es wird ein

        //OnRefreshListener festgelegt der bei aktivierung die Methode aktualisieren aufruft

        //--------------------------------------------------------Swipe Layout---------------------------------------------------------




        //------------------------------Mehrere Methoden die zum Start ben??tigt werden, werden aufgerufen------------------------------

        speichernlayouts();

        //In der ArrayList alleStunden wird jedes TextView geschpeichert und mit Listenern ausgestattet


        setTage();

        //Alles TextViews(Stunden) bekommen zugewiesen welchen Tag sie haben

        setzeZeiten();

        //Den TextViews der 1.Zeile wird ein Text zugewiesen, der besagt zu welcher Uhrzeit diese Stunde beginnt



        laden_einstellungen();

        //Es wird aus den SharedPreferences geladen welche Klasse eingestellt wurde


        Laden();

        //Beim Start werden alle Daten aus den SharedPrefreferneces geladen und auf den TextViews wieder angzeigt

        widget_speichern();

        //Alle Facher und deren R??ume werden in SharedPreferences geschpeichert um sie dem Widget zur Verf??gung zu stellen

        WidgetProvider.updateWidget(context);

        //Das Widget wird mit neuen Informationen geupdatet

        //------------------------------Mehrere Methoden die zum Start ben??tigt werden, werden aufgerufen------------------------------



        //------------------------------------Der Werbung Block wird geschpeichert und eingestellt-------------------------------------



        //Das AdView wird geschpeichert und eigestellt

        SharedPreferences settings=getSharedPreferences("ADPREFS",0);

        String addys= settings.getString("adwords",null);

        if(addys != null){

            if (addys.equals("aus")) {

                adView.setVisibility(View.GONE);

            }

        }

        //Es wir ??berp??ft ob die Werbung mit dem Code ausgeschaltet wurde

        //------------------------------------Der Werbung Block wird geschpeichert und eingestellt-------------------------------------
        final SharedPreferences klasse=getSharedPreferences("Einstellungen",0);
        String einstellung =klasse.getString("words",null);

        if (einstellung==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Willkommen bei Stundenplan 2.0. \nBitte w??hle deine Klasse.");
            List<String> Lines = Arrays.asList(getResources().getStringArray(R.array.klassen));

            String[] array = Lines.toArray(new String[Lines.size()]);

            builder.setItems(array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    List<String> Lines = Arrays.asList(getResources().getStringArray(R.array.klassen));
                    String[] array = Lines.toArray(new String[Lines.size()]);

                    MainActivity.klasse=array[which];
                    SharedPreferences settings=getSharedPreferences("Einstellungen",0);
                    SharedPreferences.Editor editor=settings.edit();

                    editor.putString("words",MainActivity.klasse);

                    editor.commit();

                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();


        }

        final SharedPreferences klasse2=getSharedPreferences("Einstellungen",0);
        String einstellung2 =klasse2.getString("words",null);
MainActivity.klasse=einstellung2;

    }


//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override//Muss f??r das Men?? vorhanden sein. In der Variable mymenu wird das Meu geschpeichert
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mymenu=menu;
        return true;
    }

    //kontrolliert ob ein neuer Vertretungsplan zur Verf??gung steht und vergleicht diesen mit dem Stundenplan
    public void aktualisieren(){

        widget_speichern();

        vertreungsplan_daten.clear();
        new MyTask(this).execute();

        new MyTask2(this).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       switch(item.getItemId()) {
            case R.id.action_refresh:


                widget_speichern();

                vertreungsplan_daten.clear();




                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView)inflater.inflate(R.layout.ic_refresh, null);
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refrsh);
                rotation.setRepeatCount(Animation.INFINITE);
                iv.startAnimation(rotation);
                item.setActionView(iv);



                new MyTask(this).execute();

                new MyTask2(this).execute();



                return true;
        }

        if(id==R.id.einstellung){



            Intent intent = new Intent(MainActivity.this, Einstellungen.class);

            startActivity(intent);
        }


        if(id==R.id.action_settings2){

          Intent intent = new Intent(MainActivity.this, Klausurplan.class);

            startActivity(intent);
        }
        if(id==R.id.pdf){
            try {
                createPdfWrapper();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        }
        if (id == R.id.action_settings) {


           AlertDialog.Builder builder = new AlertDialog.Builder(this);

         //   builder.setTitle("Confirm");
            builder.setMessage("Wollen sie alle Stunden l??schen?");
            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {


                    SharedPreferences settings=getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor=settings.edit();
                    editor.clear();
                    editor.commit();
                    l??schen();
                    for(int i=0;i<MainActivity.alleStunden.size();i++){
                        MainActivity.alleStunden.get(i).l??schen();
                    }
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });



            AlertDialog alert = builder.create();
            alert.show();



            return true;
        }
        if (id == R.id.testy) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Code eingeben!");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for frankensterzenbach, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (input.getText().toString().equals("1234")){
                        Toast.makeText(getApplicationContext(), "Hallo", Toast.LENGTH_SHORT).show();
                    }
                    if (input.getText().toString().equals("84110")){
                        Toast.makeText(getApplicationContext(), "Hallo", Toast.LENGTH_SHORT).show();
                    }
                    if (input.getText().toString().equals("vikings")){
                        Toast.makeText(getApplicationContext(), "Superbowl", Toast.LENGTH_SHORT).show();
                    }
                    if (input.getText().toString().equals("fuckgoogle")){
                        Toast.makeText(getApplicationContext(), "Ads aus", Toast.LENGTH_SHORT).show();
                        adView.setVisibility(View.GONE);

                        SharedPreferences settings=getSharedPreferences("ADPREFS",0);
                        SharedPreferences.Editor editor=settings.edit();

                        editor.putString("adwords","aus");

                        editor.commit();
                    }
                    if (input.getText().toString().equals("sorry")){
                        Toast.makeText(getApplicationContext(), "Ads an", Toast.LENGTH_SHORT).show();
                        adView.setVisibility(View.VISIBLE);

                        SharedPreferences settings=getSharedPreferences("ADPREFS",0);
                        SharedPreferences.Editor editor=settings.edit();

                        editor.putString("adwords","an");

                        editor.commit();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }
        if (id == R.id.kon){
            Intent intent = new Intent(MainActivity.this, SendMailActivity.class);

            startActivity(intent);

        }
        if (id == R.id.std){
            Intent intent = new Intent(MainActivity.this, StundenSetzen.class);

            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
    public void setzeZeiten(){

        MainActivity.alleStunden.get(0).setText("7:50"+"\n"+"-"+"\n8:35");
        MainActivity.alleStunden.get(6).setText("8:40"+"\n"+"-"+"\n9:25");
        MainActivity.alleStunden.get(12).setText("9:45"+"\n"+"-"+"\n10:30");
        MainActivity.alleStunden.get(18).setText("10:35"+"\n"+"-"+"\n11:20");
        MainActivity.alleStunden.get(24).setText("11:40"+"\n"+"-"+"\n12:25");
        MainActivity.alleStunden.get(30).setText("12:30"+"\n"+"-"+"\n13:15");
        MainActivity.alleStunden.get(36).setText("13:20"+"\n"+"-"+"\n14:10");
        MainActivity.alleStunden.get(42).setText("14:15"+"\n"+"-"+"\n15:00");
        MainActivity.alleStunden.get(48).setText("15:05"+"\n"+"-"+"\n15:50");
        MainActivity.alleStunden.get(54).setText("15:55"+"\n"+"-"+"\n16:40");
        MainActivity.alleStunden.get(60).setText("16:45"+"\n"+"-"+"\n17:30");


    }
    public void onClick(View e) {

        if (e.getId() == text65 || e.getId() == text4 || e.getId() == text16 || e.getId() == text102 || e.getId() == text26 || e.getId() == text31 || e.getId() == text37 || e.getId() == text41 || e.getId() == text46 || e.getId() == text108 || e.getId() == text56) {
            // TODO Auto-generated method stub



        } else {

            for (int i = 0; i < alleStunden.size(); i++) {

                if (e.equals(alleStunden.get(i))) {
                    bearbeiten = alleStunden.get(i);
                    bearbeiteni = i;
                }
            }if(!bearbeiten.fach.equals("")){
                Intent intent = new Intent(MainActivity.this, Stunde_info.class);

                startActivity(intent);
            }else{
                Intent intent = new Intent(MainActivity.this, neueStunde_java.class);

                startActivity(intent);
            }






        }
    }
    public void setTage(){
        for(int i=0;i<alleStunden.size();i++){
            alleStunden.get(i+1).tag="Montag";
            alleStunden.get(i+2).tag="Dienstag";
            alleStunden.get(i+3).tag="Mittwoch";
            alleStunden.get(i+4).tag="Donnerstag";
            alleStunden.get(i+5).tag="Freitag";
            i=i+5;


        }
    }
    public void   Laden(){


        SharedPreferences settings=getSharedPreferences("PREFS",0);


        String test= settings.getString("words",null);

if(test!=null){
        String[]itemwors=test.split(",");
        ArrayList<String>worte=new ArrayList<>();
        for(int i=0;i<itemwors.length;i++) {

            worte.add(itemwors[i]);
            speichern_laden = worte;


        }
        }



        umwandelnzuruck();



    }
    public void umwandelnzuruck() {
        int platz;



        if(speichern_laden.size()!=0) {
            for (int i = 0; i < speichern_laden.size(); i++) {
                platz=Integer.parseInt(speichern_laden.get(i+5));



                alleStunden.get(platz).setText(speichern_laden.get(i+6)+"\n"+"\n"+speichern_laden.get(i+7));
                alleStunden.get(platz).farbe =""+ speichern_laden.get(i+1);
                alleStunden.get(platz).kurs = speichern_laden.get(i + 2);
                alleStunden.get(platz).nummer = speichern_laden.get(i + 3);
                alleStunden.get(platz).datum = speichern_laden.get(i + 4);

                alleStunden.get(platz).platz = speichern_laden.get(i + 5);
                alleStunden.get(platz).fach = speichern_laden.get(i + 6);
                alleStunden.get(platz).raum=speichern_laden.get(i+7);
                alleStunden.get(platz).lehrer=speichern_laden.get(i+8);

                alleStunden.get(platz).aktualisieren();

                i = i + 8;



            }
        }


    }
    public void speichernlayouts(){
        LinearLayout  layout1 = (LinearLayout) findViewById( R.id.layout1 );
        LinearLayout  layout2 = (LinearLayout) findViewById( R.id.layout2 );
        LinearLayout  layout3 = (LinearLayout) findViewById( R.id.layout3 );
        LinearLayout  layout4 = (LinearLayout) findViewById( R.id.layout4 );
        LinearLayout  layout5 = (LinearLayout) findViewById( R.id.layout5 );
        LinearLayout  layout6 = (LinearLayout) findViewById( R.id.layout6 );
        LinearLayout  layout7 = (LinearLayout) findViewById( R.id.layout7 );
        LinearLayout  layout8 = (LinearLayout) findViewById( R.id.layout8 );
        LinearLayout  layout9 = (LinearLayout) findViewById( R.id.layout9 );
        LinearLayout  layout10 = (LinearLayout) findViewById( R.id.layout10 );
        LinearLayout  layout11 = (LinearLayout) findViewById( R.id.layout11 );




            for (int i = 0; i < layout1.getChildCount(); i++) {
                if (layout1.getChildAt(i) instanceof TextView2) {
                    TextView2 textview=(TextView2)layout1.getChildAt(i);
                    textview.stunde="1";
                    alleStunden.add((TextView2) layout1.getChildAt(i));
                    layout1.getChildAt(i).setOnClickListener(this);
                    layout1.getChildAt(i).setOnLongClickListener(this);


                }}
        for (int i = 0; i < layout2.getChildCount(); i++) {
            if (layout2.getChildAt(i) instanceof TextView2) {
                TextView2 textview=(TextView2)layout2.getChildAt(i);
                textview.stunde="2";
                alleStunden.add((TextView2) layout2.getChildAt(i));
                layout2.getChildAt(i).setOnClickListener(this);
                layout2.getChildAt(i).setOnLongClickListener(this);
            }}
        for (int i = 0; i < layout3.getChildCount(); i++) {
            if (layout3.getChildAt(i) instanceof TextView2) {
                TextView2 textview=(TextView2)layout3.getChildAt(i);
                textview.stunde="3";
                alleStunden.add((TextView2) layout3.getChildAt(i));
                layout3.getChildAt(i).setOnClickListener(this);
                layout3.getChildAt(i).setOnLongClickListener(this);
            }}
        for (int i = 0; i < layout4.getChildCount(); i++) {
            if (layout4.getChildAt(i) instanceof TextView2) {
                TextView2 textview=(TextView2)layout4.getChildAt(i);
                textview.stunde="4";
                alleStunden.add((TextView2) layout4.getChildAt(i));
                layout4.getChildAt(i).setOnClickListener(this);
                layout4.getChildAt(i).setOnLongClickListener(this);
            }}
        for (int i = 0; i < layout5.getChildCount(); i++) {
            TextView2 textview=(TextView2)layout5.getChildAt(i);
            textview.stunde="5";
            if (layout5.getChildAt(i) instanceof TextView2) {
                alleStunden.add((TextView2) layout5.getChildAt(i));
                layout5.getChildAt(i).setOnClickListener(this);
                layout5.getChildAt(i).setOnLongClickListener(this);
            }}
        for (int i = 0; i < layout6.getChildCount(); i++) {
            if (layout6.getChildAt(i) instanceof TextView2) {
                TextView2 textview=(TextView2)layout6.getChildAt(i);
                textview.stunde="6";
                alleStunden.add((TextView2) layout6.getChildAt(i));
                layout6.getChildAt(i).setOnClickListener(this);
                layout6.getChildAt(i).setOnLongClickListener(this);
            }}
        for (int i = 0; i < layout7.getChildCount(); i++) {
            if (layout7.getChildAt(i) instanceof TextView2) {
                TextView2 textview=(TextView2)layout7.getChildAt(i);
                textview.stunde="7";
                alleStunden.add((TextView2) layout7.getChildAt(i));
                layout7.getChildAt(i).setOnClickListener(this);
                layout7.getChildAt(i).setOnLongClickListener(this);
            }}
        for (int i = 0; i < layout8.getChildCount(); i++) {
            TextView2 textview=(TextView2)layout8.getChildAt(i);
            textview.stunde="8";
            if (layout8.getChildAt(i) instanceof TextView2) {
                alleStunden.add((TextView2) layout8.getChildAt(i));
                layout8.getChildAt(i).setOnClickListener(this);
                layout8.getChildAt(i).setOnLongClickListener(this);
            }}
        for (int i = 0; i < layout9.getChildCount(); i++) {
            if (layout9.getChildAt(i) instanceof TextView2) {
                TextView2 textview=(TextView2)layout9.getChildAt(i);
                textview.stunde="9";
                alleStunden.add((TextView2) layout9.getChildAt(i));
                layout9.getChildAt(i).setOnClickListener(this);
                layout9.getChildAt(i).setOnLongClickListener(this);
            }}
        for (int i = 0; i < layout10.getChildCount(); i++) {
            TextView2 textview=(TextView2)layout10.getChildAt(i);
            textview.stunde="10";
            if (layout10.getChildAt(i) instanceof TextView2) {
                alleStunden.add((TextView2) layout10.getChildAt(i));
                layout10.getChildAt(i).setOnClickListener(this);
                layout10.getChildAt(i).setOnLongClickListener(this);
            }}
        for (int i = 0; i < layout11.getChildCount(); i++) {
            TextView2 textview=(TextView2)layout11.getChildAt(i);
            textview.stunde="11";
            if (layout11.getChildAt(i) instanceof TextView2) {
                alleStunden.add((TextView2) layout11.getChildAt(i));
                layout11.getChildAt(i).setOnClickListener(this);
                layout11.getChildAt(i).setOnLongClickListener(this);
            }}

















    }
    @Override
    public boolean onLongClick(View view) {
        for (int i = 0; i < alleStunden.size(); i++) {

            if (view.equals(alleStunden.get(i))) {
                bearbeiten = alleStunden.get(i);
                bearbeiteni = i;

            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);



        builder.setMessage("Wollen sie diese Stunde l??schen?");
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                bearbeiten.l??schen();
                speichern();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }



    public  void speichern(){
        umwandelnhin();
        if(speichern_laden.size()>0){
        StringBuilder stringBuilder=new StringBuilder();
        for(String s: MainActivity.speichern_laden){

            stringBuilder.append(s);
            stringBuilder.append(",");


        }


        SharedPreferences settings=getSharedPreferences("PREFS",0);
        SharedPreferences.Editor editor=settings.edit();

        editor.putString("words",stringBuilder.toString());

        editor.commit();

    }else if(speichern_laden.size()==0){
            SharedPreferences settings=getSharedPreferences("raume",0);
            SharedPreferences.Editor editor=settings.edit();
            editor.clear();
            editor.commit();
    }

    }
    public void l??schen(){
        SharedPreferences settings=getSharedPreferences("PREFS",0);
        SharedPreferences.Editor editor=settings.edit();
        editor.clear();
        editor.commit();
    }
    public void umwandelnhin() {
        ArrayList<String> t=new ArrayList<String>();
        speichern_laden=t;

        for (int i = 0; i < alleStunden.size(); i++) {

if(!alleStunden.get(i).farbe.equals("")) {

    speichern_laden.add(String.valueOf(alleStunden.get(i).getText()));
    speichern_laden.add(String.valueOf(alleStunden.get(i).farbe));
    speichern_laden.add(String.valueOf(alleStunden.get(i).kurs));
    speichern_laden.add(String.valueOf(alleStunden.get(i).nummer));
    speichern_laden.add(String.valueOf(alleStunden.get(i).datum));
    speichern_laden.add(String.valueOf(alleStunden.get(i).platz));
    speichern_laden.add(String.valueOf(alleStunden.get(i).fach));
    speichern_laden.add(String.valueOf(alleStunden.get(i).raum));
    speichern_laden.add(String.valueOf(alleStunden.get(i).lehrer));



}








        }


    }
    public static  void check() {



        for (int i = 0; i < MainActivity.alleStunden.size(); i++) {

if(MainActivity.klasse.equals("Q1")||MainActivity.klasse.equals("EF")||MainActivity.klasse.equals("Q2")) {
    for (int m = 0; m < MainActivity.vertreungsplan_daten.size(); m++) {
        Log.d("Tach",MainActivity.alleStunden.get(i).fach);
        if (MainActivity.vertreungsplan_daten.get(m).fach.equals(MainActivity.alleStunden.get(i).fach) && MainActivity.vertreungsplan_daten.get(m).kursid.equals(MainActivity.alleStunden.get(i).nummer) && MainActivity.vertreungsplan_daten.get(m).kurs.equals(MainActivity.alleStunden.get(i).kurs) && MainActivity.vertreungsplan_daten.get(m).tag.equals(MainActivity.alleStunden.get(i).tag) && MainActivity.vertreungsplan_daten.get(m).stunde.equals(MainActivity.alleStunden.get(i).stunde)) {

            if (vertreungsplan_daten.get(m).text.equals("Selbstlernen")) {
                if (MainActivity.alleStunden.get(i).fach.length() < 9) {
                    MainActivity.alleStunden.get(i).setText("" + alleStunden.get(i).fach + "\nFrei");
                } else {
                    MainActivity.alleStunden.get(i).setText("" + alleStunden.get(i).fach + " " + "Frei");
                }
                MainActivity.alleStunden.get(i).setTextColor(Color.RED);
                MainActivity.alleStunden.get(i).aktualisieren2();


            } else if (vertreungsplan_daten.get(m).text.equals("Vertretung")) {
                MainActivity.alleStunden.get(i).setText("" + alleStunden.get(i).fach + " " + vertreungsplan_daten.get(m).text);
                MainActivity.alleStunden.get(i).setTextColor(Color.RED);
                MainActivity.alleStunden.get(i).aktualisieren2();


            } else {
                MainActivity.alleStunden.get(i).setText("" + alleStunden.get(i).fach + " " + vertreungsplan_daten.get(m).text + " " + vertreungsplan_daten.get(m).raum);
                MainActivity.alleStunden.get(i).setTextColor(Color.RED);
                MainActivity.alleStunden.get(i).aktualisieren2();

            }

        }
    }
}else{
    for (int m = 0; m < MainActivity.vertreungsplan_daten.size(); m++) {
        if (MainActivity.vertreungsplan_daten.get(m).fach.equals(MainActivity.alleStunden.get(i).fach) &&MainActivity.vertreungsplan_daten.get(m).tag.equals(MainActivity.alleStunden.get(i).tag) && MainActivity.vertreungsplan_daten.get(m).stunde.equals(MainActivity.alleStunden.get(i).stunde)) {

            if (vertreungsplan_daten.get(m).text.equals("Frei")) {
                if (MainActivity.alleStunden.get(i).fach.length() < 9) {
                    MainActivity.alleStunden.get(i).setText("" + alleStunden.get(i).fach + "\nFrei");
                } else {
                    MainActivity.alleStunden.get(i).setText("" + alleStunden.get(i).fach + " " + "Frei");
                }
                MainActivity.alleStunden.get(i).setTextColor(Color.RED);
                MainActivity.alleStunden.get(i).aktualisieren2();


            } else if (vertreungsplan_daten.get(m).text.equals("Statt-Vertretung")) {
                MainActivity.alleStunden.get(i).setText("" + alleStunden.get(i).fach + " \n" + "VTR.\n"+vertreungsplan_daten.get(m).raum);

                MainActivity.alleStunden.get(i).setTextColor(Color.RED);
                MainActivity.alleStunden.get(i).aktualisieren2();


            } else {
                MainActivity.alleStunden.get(i).setText("" + alleStunden.get(i).fach + " " + vertreungsplan_daten.get(m).text + " " + vertreungsplan_daten.get(m).raum);
                MainActivity.alleStunden.get(i).setTextColor(Color.RED);
                MainActivity.alleStunden.get(i).aktualisieren2();

            }

        }
    }

}

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        finish();
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }



    class MyTask2 extends AsyncTask<Void,Void,Void>{
        public String code2="";
       Calendar datumheute = Calendar.getInstance();

        private Context mCon;

        public MyTask2(Context con)
        {
            mCon = con;
        }
        @Override
        protected Void doInBackground(Void... voids) {

   for(int l=0;l<5;l++) {
    SimpleDateFormat format1 = new SimpleDateFormat("yyMMdd");
    String formatted = format1.format(datumheute.getTime());
    try {
        String code = "http://www.ohg-bensberg.de/WSK_extdata/vplan/" + formatted + "/Ver_Kla_" + klasse + ".htm";
        code2=code;
        org.jsoup.nodes.Document doc = Jsoup.connect(code).ignoreHttpErrors(true).get();

        if (!doc.title().equals("Object not found!")) {
            Elements table = doc.select("center font table");
            int m=table.size();
            if(m<3){
                MainActivity.c=code2;
                Intent i = new Intent(context, PlanWebView.class);
                i.putExtra("epuzzle", MainActivity.c);
                context.startActivity(i);

            }
            else {

            Element tables = doc.select("center font table").get(1);
            Elements rows = tables.select("tr");
            // z??hlt wie viele
            // spalten
            // es
            // gibt
            int spalten = rows.size() + 1;


                for (int i = 2; i < spalten; i++) {

                    String test = ("center font tbody tr:nth-child(" + i + ") td:nth-child(6)");

                    String hallo = ("center font tbody tr:nth-child(" + i + ") td:nth-child(3)");
                    String art = ("center font tbody tr:nth-child(" + i + ") td:nth-child(9)");
                    String raum = ("center font tbody tr:nth-child(" + i + ") td:nth-child(8)");


                    Elements values = doc.select(test);
                    for (Element elem : values) {
                        fach = elem.text();


                    }

                    Elements values4 = doc.select(raum);
                    for (Element elem : values4) {
                        raum2 = elem.text();


                    }

                    Elements values3 = doc.select(art);
                    for (Element elem : values3) {
                        text = elem.text();


                    }
                    Elements values2 = doc.select(hallo);
                    for (Element elem : values2) {
                        stunde = elem.text();


                    }

                    String c = kurs;


                    if (!fach.equals(test3) && !fach.equals("---")) {
                        if (fach.length() == 6) {

                            kurs = String.valueOf(fach.charAt(3));
                            kursid = String.valueOf(fach.charAt(5));

                        } else if (fach.length() == 5) {
                            Log.d("Moin",fach);
                            kurs = String.valueOf(fach.charAt(2));

                            kursid = String.valueOf(fach.charAt(4));
                            Log.d("Moin2",kursid);
                        }
                        if (fach.equals("---")) {
                            kurs = "---";
                            kursid = "---";
                        }
                        if (kurs == null) {
                            kurs = "";
                        }

if(fach.length()==6){
    fach = String.valueOf(fach.charAt(0) + String.valueOf(fach.charAt(1)));
}else if(fach.length()==5){
    fach = String.valueOf(fach.charAt(0));
}



                        c = kurs;
                        StundeVplan vplan;

                        if (stunde.length() == 1) {
                            vplan = new StundeVplan(fach, datumheute, c, kursid, text, raum2, stunde);
                            MainActivity.vertreungsplan_daten.add(vplan);
                        } else if (stunde.length() == 5) {
                            String stunde1 = String.valueOf(stunde.charAt(0));
                            String stunde2 = String.valueOf(stunde.charAt(4));
                            vplan = new StundeVplan(fach, datumheute, c, kursid, text, raum2, stunde1);
                            MainActivity.vertreungsplan_daten.add(vplan);

                            vplan = new StundeVplan(fach, datumheute, c, kursid, text, raum2, stunde2);
                            MainActivity.vertreungsplan_daten.add(vplan);


                        } else if (stunde.length() == 7) {
                            String stunde1 = String.valueOf(stunde.charAt(0) + String.valueOf(stunde.charAt(1)));
                            String stunde2 = String.valueOf(stunde.charAt(5) + String.valueOf(stunde.charAt(6)));
                            vplan = new StundeVplan(fach, datumheute, c, kursid, text, raum2, stunde1);
                            MainActivity.vertreungsplan_daten.add(vplan);

                            vplan = new StundeVplan(fach, datumheute, c, kursid, text, raum2, stunde2);
                            MainActivity.vertreungsplan_daten.add(vplan);


                        }


                    }

                }
            }

        }


    } catch (IOException e) {
        e.printStackTrace();
    }

    datumheute.add(Calendar.DATE, 1);
}


            return null;
        }
        public void Toast(String ptext){
            Context context = getApplicationContext();
            CharSequence text = ptext;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }



        @Override
        protected void onPostExecute(Void aVoid) {

           check();

            ((MainActivity) mCon).resetUpdating();





        }
    }


    private void createPdfWrapper() throws FileNotFoundException,DocumentException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }


                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }else {
            createPdf();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            //Log.i(TAG, "Created a new directory for PDF");
        }
        Toast.makeText(context, "Geschpeichert bei: "+docsFolder, Toast.LENGTH_LONG).show();

        pdfFile = new File(docsFolder.getAbsolutePath(),"Stundenplan.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(createTable2());






        document.close();


    }




    public static PdfPTable createTable2() throws DocumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String tag = sdf.format(new Date());
        PdfPTable table = new PdfPTable(6);

        table.setWidths(new int[]{1, 1, 1,1,1,1});
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("Stundenplan vom "+tag+", Klasse: "+MainActivity.klasse));
        cell.setColspan(6);
        table.addCell(cell);




        table.addCell("Stunden");
        table.addCell("Montag");
        table.addCell("Dienstag");

        table.addCell("Mittowch");
        table.addCell("Donnerstag");
        table.addCell("Freitag");
        PdfPCell[] cells = table.getRow(1).getCells();
        for (int j=0;j<cells.length;j++){
            cells[j].setBackgroundColor(BaseColor.GRAY);

        }

        for(int i=0;i<alleStunden.size();i++){
            if(i==0||i==6||i==12||i==18||i==24||i==30||i==36||i==42||i==48||i==54||i==60){
                table.addCell("" + alleStunden.get(i).getText());
            }else {
                table.addCell("" + alleStunden.get(i).fach + "\n" + alleStunden.get(i).raum);
            }
        }


        return table;
    }
    private void sendTokenToServer() {


        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String email ="test";

        if (token == null) {


            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }






    class MyTask extends AsyncTask<Void,Void,Void>{

        private Context mCon;

        public MyTask(Context con)
        {
            mCon = con;
        }
        public void Toast(String ptext){
            Context context = getApplicationContext();
            CharSequence text = ptext;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Calendar c=Calendar.getInstance();

         try{



            SimpleDateFormat format1 = new SimpleDateFormat("yyMMdd");


            String formatted = format1.format(c.getTime());
            String code = "http://www.ohg-bensberg.de/WSK_extdata/vplan/"+formatted+"/Ver_Kla_"+klasse+".htm";

            org.jsoup.nodes.Document doc = Jsoup.connect(code).ignoreHttpErrors(true).get();

            if (!doc.title().equals("Object not found!")) {
                heute=true;


            }else{
                heute=false;



            }


            c.add(Calendar.DATE,1);
            formatted = format1.format(c.getTime());
            code = "http://www.ohg-bensberg.de/WSK_extdata/vplan/"+formatted+"/Ver_Kla_"+klasse+".htm";

            doc = null;
            try {
                doc = Jsoup.connect(code).ignoreHttpErrors(true).get();
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (!doc.title().equals("Object not found!")) {
                morgen=true;


            }else{
                morgen=false;




            }


             Calendar calendar=Calendar.getInstance();

             String weekDay;
             SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);




             weekDay = dayFormat.format(calendar.getTime());


             if(weekDay.equals("Friday")){

                 c.add(Calendar.DATE,1);
                 c.add(Calendar.DATE,1);

                 formatted = format1.format(c.getTime());

                 code = "http://www.ohg-bensberg.de/WSK_extdata/vplan/"+formatted+"/Ver_Kla_"+klasse+".htm";

                 doc = null;
                 try {
                     doc = Jsoup.connect(code).ignoreHttpErrors(true).get();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }


                 if (!doc.title().equals("Object not found!")) {
                     montag=true;


                 }else{
                     montag=false;




                 }
             }
             if(weekDay.equals("Saturday")){

                 c.add(Calendar.DATE,1);


                 formatted = format1.format(c.getTime());

                 code = "http://www.ohg-bensberg.de/WSK_extdata/vplan/"+formatted+"/Ver_Kla_"+klasse+".htm";

                 doc = null;
                 try {
                     doc = Jsoup.connect(code).ignoreHttpErrors(true).get();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }


                 if (!doc.title().equals("Object not found!")) {
                     montag=true;


                 }else{
                     montag=false;




                 }
             }
             if(weekDay.equals("Sunday")){




                 formatted = format1.format(c.getTime());

                 code = "http://www.ohg-bensberg.de/WSK_extdata/vplan/"+formatted+"/Ver_Kla_"+klasse+".htm";

                 doc = null;
                 try {
                     doc = Jsoup.connect(code).ignoreHttpErrors(true).get();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }


                 if (!doc.title().equals("Object not found!")) {
                     montag=true;


                 }else{
                     montag=false;




                 }
             }
         }catch (IOException e){
             e.printStackTrace();
         }


            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {

            Context context;
            CharSequence text ;
            int duration ;

            Toast toast;


            l.setRefreshing(false);

            if (heute == true && morgen == false&&montag==false) {
                context = getApplicationContext();
                text = "Der Vertretungsplan f??r Heute ist verf??gbar!";
                duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (heute == true && morgen == false&&montag==true) {
                context = getApplicationContext();
                text = "Der Vertretungsplan f??r Heute und Montag ist verf??gbar!";
                duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (morgen == true && heute == false&&montag==false) {
                context = getApplicationContext();
                text = "Der Vertretungsplan ist f??r Morgen verf??gbar!";
                duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (morgen == true && heute == false&&montag==true) {
                context = getApplicationContext();
                text = "Der Vertretungsplan ist f??r Morgen und Montag verf??gbar!";
                duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (heute == true && morgen == true&&montag==false) {
                context = getApplicationContext();
                text = "Der Vertretungsplan f??r Heute und Morgen ist verf??gbar!";
                duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (heute == true && morgen == true&&montag==true) {
                context = getApplicationContext();
                text = "Der Vertretungsplan f??r Heute und Morgen und Montag ist verf??gbar!";
                duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (heute == false && morgen == false&&montag ==false) {
                context = getApplicationContext();

                text = "Der Vertretungsplan f??r Heute und Morgen ist noch nicht verf??gbar oder du hast kein Internetzugriff!";
                duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (heute == false && morgen == false&&montag ==true) {
                context = getApplicationContext();

                text = "Der Vertretungsplan f??r Montag ist verf??gbar!";
                duration = Toast.LENGTH_SHORT;

                toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            heute = false;
            morgen = false;
            montag=false;



        }


    }
    public void resetUpdating() {
// Get our refresh item from the menu
        MenuItem m = mymenu.findItem(R.id.action_refresh);
        if(m.getActionView()!=null)
        {
// Remove the animation.
            m.getActionView().clearAnimation();
            m.setActionView(null);
        }
    }

 public void laden_einstellungen(){
        SharedPreferences settings=getSharedPreferences("Einstellungen",0);


        String test= settings.getString("words2",null);
        klasse=test;
    }
    public   void  widget_speichern(){

        ArrayList<String> fach=new ArrayList<>();
        ArrayList<String> raum=new ArrayList<>();

//alle facher informationen
        fach.clear();

        fach.add(MainActivity.alleStunden.get(1).fach);
        fach.add(MainActivity.alleStunden.get(7).fach);
        fach.add(MainActivity.alleStunden.get(13).fach);
        fach.add(MainActivity.alleStunden.get(19).fach);
        fach.add(MainActivity.alleStunden.get(25).fach);
        fach.add(MainActivity.alleStunden.get(31).fach);
        fach.add(MainActivity.alleStunden.get(37).fach);
        fach.add(MainActivity.alleStunden.get(43).fach);
        fach.add(MainActivity.alleStunden.get(49).fach);
        fach.add(MainActivity.alleStunden.get(55).fach);
        fach.add(MainActivity.alleStunden.get(61).fach);



        fach.add(MainActivity.alleStunden.get(2).fach);
        fach.add(MainActivity.alleStunden.get(8).fach);
        fach.add(MainActivity.alleStunden.get(14).fach);
        fach.add(MainActivity.alleStunden.get(20).fach);
        fach.add(MainActivity.alleStunden.get(26).fach);
        fach.add(MainActivity.alleStunden.get(32).fach);
        fach.add(MainActivity.alleStunden.get(38).fach);
        fach.add(MainActivity.alleStunden.get(44).fach);
        fach.add(MainActivity.alleStunden.get(50).fach);
        fach.add(MainActivity.alleStunden.get(56).fach);
        fach.add(MainActivity.alleStunden.get(62).fach);



        fach.add(MainActivity.alleStunden.get(3).fach);
        fach.add(MainActivity.alleStunden.get(9).fach);
        fach.add(MainActivity.alleStunden.get(15).fach);
        fach.add(MainActivity.alleStunden.get(21).fach);
        fach.add(MainActivity.alleStunden.get(27).fach);
        fach.add(MainActivity.alleStunden.get(33).fach);
        fach.add(MainActivity.alleStunden.get(39).fach);
        fach.add(MainActivity.alleStunden.get(45).fach);
        fach.add(MainActivity.alleStunden.get(51).fach);
        fach.add(MainActivity.alleStunden.get(57).fach);
        fach.add(MainActivity.alleStunden.get(63).fach);



        fach.add(MainActivity.alleStunden.get(4).fach);
        fach.add(MainActivity.alleStunden.get(10).fach);
        fach.add(MainActivity.alleStunden.get(16).fach);
        fach.add(MainActivity.alleStunden.get(22).fach);
        fach.add(MainActivity.alleStunden.get(28).fach);
        fach.add(MainActivity.alleStunden.get(34).fach);
        fach.add(MainActivity.alleStunden.get(40).fach);
        fach.add(MainActivity.alleStunden.get(46).fach);
        fach.add(MainActivity.alleStunden.get(52).fach);
        fach.add(MainActivity.alleStunden.get(58).fach);
        fach.add(MainActivity.alleStunden.get(64).fach);



        fach.add(MainActivity.alleStunden.get(5).fach);
        fach.add(MainActivity.alleStunden.get(11).fach);
        fach.add(MainActivity.alleStunden.get(17).fach);
        fach.add(MainActivity.alleStunden.get(23).fach);
        fach.add(MainActivity.alleStunden.get(29).fach);
        fach.add(MainActivity.alleStunden.get(35).fach);
        fach.add(MainActivity.alleStunden.get(41).fach);
        fach.add(MainActivity.alleStunden.get(47).fach);
        fach.add(MainActivity.alleStunden.get(53).fach);
        fach.add(MainActivity.alleStunden.get(59).fach);
        fach.add(MainActivity.alleStunden.get(65).fach);

        for(int i=0;i<fach.size();i++){
            if(fach.get(i).equals("")){
                fach.set(i," ");
            }
        }


        //fach
        if(fach.size()>0){
            StringBuilder stringBuilder=new StringBuilder();
            for(String s: fach){

                stringBuilder.append(s);
                stringBuilder.append(",");


            }



            SharedPreferences settings=getSharedPreferences("fach",0);
            SharedPreferences.Editor editor=settings.edit();

            editor.putString("fach",stringBuilder.toString());

            editor.commit();

        }else if(fach.size()==0){
            SharedPreferences settings=getSharedPreferences("fach",0);
            SharedPreferences.Editor editor=settings.edit();
            editor.clear();
            editor.commit();
        }



        //alle raum informationen


        raum.clear();

        raum.add(MainActivity.alleStunden.get(1).raum);
        raum.add(MainActivity.alleStunden.get(7).raum);
        raum.add(MainActivity.alleStunden.get(13).raum);
        raum.add(MainActivity.alleStunden.get(19).raum);
        raum.add(MainActivity.alleStunden.get(25).raum);
        raum.add(MainActivity.alleStunden.get(31).raum);
        raum.add(MainActivity.alleStunden.get(37).raum);
        raum.add(MainActivity.alleStunden.get(43).raum);
        raum.add(MainActivity.alleStunden.get(49).raum);
        raum.add(MainActivity.alleStunden.get(55).raum);
        raum.add(MainActivity.alleStunden.get(61).raum);



        raum.add(MainActivity.alleStunden.get(2).raum);
        raum.add(MainActivity.alleStunden.get(8).raum);
        raum.add(MainActivity.alleStunden.get(14).raum);
        raum.add(MainActivity.alleStunden.get(20).raum);
        raum.add(MainActivity.alleStunden.get(26).raum);
        raum.add(MainActivity.alleStunden.get(32).raum);
        raum.add(MainActivity.alleStunden.get(38).raum);
        raum.add(MainActivity.alleStunden.get(44).raum);
        raum.add(MainActivity.alleStunden.get(50).raum);
        raum.add(MainActivity.alleStunden.get(56).raum);
        raum.add(MainActivity.alleStunden.get(62).raum);



        raum.add(MainActivity.alleStunden.get(3).raum);
        raum.add(MainActivity.alleStunden.get(9).raum);
        raum.add(MainActivity.alleStunden.get(15).raum);
        raum.add(MainActivity.alleStunden.get(21).raum);
        raum.add(MainActivity.alleStunden.get(27).raum);
        raum.add(MainActivity.alleStunden.get(33).raum);
        raum.add(MainActivity.alleStunden.get(39).raum);
        raum.add(MainActivity.alleStunden.get(45).raum);
        raum.add(MainActivity.alleStunden.get(51).raum);
        raum.add(MainActivity.alleStunden.get(57).raum);
        raum.add(MainActivity.alleStunden.get(63).raum);



        raum.add(MainActivity.alleStunden.get(4).raum);
        raum.add(MainActivity.alleStunden.get(10).raum);
        raum.add(MainActivity.alleStunden.get(16).raum);
        raum.add(MainActivity.alleStunden.get(22).raum);
        raum.add(MainActivity.alleStunden.get(28).raum);
        raum.add(MainActivity.alleStunden.get(34).raum);
        raum.add(MainActivity.alleStunden.get(40).raum);
        raum.add(MainActivity.alleStunden.get(46).raum);
        raum.add(MainActivity.alleStunden.get(52).raum);
        raum.add(MainActivity.alleStunden.get(58).raum);
        raum.add(MainActivity.alleStunden.get(64).raum);



        raum.add(MainActivity.alleStunden.get(5).raum);
        raum.add(MainActivity.alleStunden.get(11).raum);
        raum.add(MainActivity.alleStunden.get(17).raum);
        raum.add(MainActivity.alleStunden.get(23).raum);
        raum.add(MainActivity.alleStunden.get(29).raum);
        raum.add(MainActivity.alleStunden.get(35).raum);
        raum.add(MainActivity.alleStunden.get(41).raum);
        raum.add(MainActivity.alleStunden.get(47).raum);
        raum.add(MainActivity.alleStunden.get(53).raum);
        raum.add(MainActivity.alleStunden.get(59).raum);
        raum.add(MainActivity.alleStunden.get(65).raum);

        for(int i=0;i<raum.size();i++){
            if(raum.get(i).equals("")){
                raum.set(i," ");
            }
        }


        //raum
        if(raum.size()>0){
            StringBuilder stringBuilder=new StringBuilder();
            for(String s: raum){

                stringBuilder.append(s);
                stringBuilder.append(",");


            }



            SharedPreferences settings=getSharedPreferences("raum",0);
            SharedPreferences.Editor editor=settings.edit();

            editor.putString("raum",stringBuilder.toString());

            editor.commit();

        }else if(raum.size()==0){
            SharedPreferences settings=getSharedPreferences("raum",0);
            SharedPreferences.Editor editor=settings.edit();
            editor.clear();
            editor.commit();
        }

    }
}
