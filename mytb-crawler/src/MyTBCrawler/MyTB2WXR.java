package MyTBCrawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyTB2WXR {

  private PrintWriter wxr_file;
  
  public MyTB2WXR(String filename) {
    try {
      wxr_file = new PrintWriter(filename, "UTF-8");
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      e.printStackTrace();
    };
  }


  
  public void writeIntro() {
    wxr_file.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
"<rss version=\"2.0\" xmlns:excerpt=\"http://wordpress.org/export/1.2/excerpt/\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" xmlns:wfw=\"http://wellformedweb.org/CommentAPI/\"  xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:wp=\"http://wordpress.org/export/1.2/\">" +
"<channel><title>myTagebuch</title><link>http://www.mytagebuch.de/</link><description>myTagebuch</description> <pubDate>Mon, 19 Dec 2017 14:19:36 +0000</pubDate><language>de-DE</language><wp:wxr_version>1.2</wp:wxr_version><wp:base_site_url>http://www.mytagebuch.de/</wp:base_site_url>    <wp:base_blog_url>http://www.mytagebuch.de/</wp:base_blog_url>" +
"<wp:author><wp:author_id>1</wp:author_id><wp:author_login>myTagebuch</wp:author_login><wp:author_email>myTagebuch</wp:author_email><wp:author_display_name><![CDATA[myTagebuch]]></wp:author_display_name><wp:author_first_name><![CDATA[]]></wp:author_first_name><wp:author_last_name><![CDATA[]]></wp:author_last_name></wp:author>"
);        
    
  }
  
  public void writeItem(String titel, String datum, String inhalt, String eid) {
    wxr_file.println(
"        <item>" +
"        <title>" + titel + "</title>" +
"        <pubDate>" + datum + "</pubDate>" +
"        <dc:creator>myTagebuch2WXR</dc:creator>" +
"        <description></description>" +
"        <content:encoded><![CDATA[" + inhalt + "]]></content:encoded>" +
"        <excerpt:encoded><![CDATA[]]></excerpt:encoded>" +
"        <wp:post_id>" + eid + "</wp:post_id>" +
"            <wp:post_date>" + datum + "</wp:post_date>" +
"            <wp:post_date_gmt>" + datum + "</wp:post_date_gmt>" +
"            <wp:comment_status>open</wp:comment_status>" +
"            <wp:ping_status>open</wp:ping_status>" +
"            <wp:post_name>post-title</wp:post_name>" +
"            <wp:status>publish</wp:status>" +
"            <wp:post_parent>0</wp:post_parent>" +
"            <wp:menu_order>0</wp:menu_order>" +
"            <wp:post_type>post</wp:post_type>" +
"            <wp:post_password></wp:post_password>" +
"            <wp:is_sticky>0</wp:is_sticky>" +
"        <category domain=\"category\" nicename=\"myTagebuch\"><![CDATA[myTagebuch]]></category>" +
"      </item>   "); 
    
  }
  
  private void writeExtro() {
    wxr_file.println("</channel></rss>");
    wxr_file.close();
  }
  
  public static void main(String[] args) {
    Integer mytb_id;
    Integer mytb_entries;
    
    Options options = new Options();
    options.addOption("mytb_autoren_id", true, "myTagebuch Autoren id");
    options.addOption("mytb_anzahl", true, "Anzahl der myTagebuch Eintraege");
    options.addOption("export_datei", true, "Dateiname des Datenexports");     
    
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine cmd = parser.parse(options, args);
      mytb_id = new Integer(cmd.getOptionValue("mytb_autoren_id"));
      mytb_entries = new Integer(cmd.getOptionValue("mytb_anzahl"));

      // myTagebuch URL-Teil fuer den Abruf von Tagebucheintraegen (die EID am Ende wird weiter unten in der Schleife angefuegt)
      String mytb_url_eintrag = new String("http://www.mytagebuch.de/profil.php?action=eintrag&id=" + mytb_id
          + "&eid=");

      // Regular Expression um die eid aus den URLs der Liste zu filtern
      Pattern eid_regex = Pattern.compile("(\\d+)$");

      DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
      DateFormat wp_df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      MyTB2WXR wxr = new MyTB2WXR(cmd.getOptionValue("export_datei"));
      wxr.writeIntro();

      int j = 0;
      for(int i = 0; i <= mytb_entries/25; i++) {
        Integer offset = new Integer(i*25);

        // myTagebuch URL fuer die Liste der Tagebucheintraege (wir beginnen bei den aeltesten)
        String mytb_url_liste = new String("http://www.mytagebuch.de/profil.php?action=alleeintraege&offset=" 
            + offset 
            + "&id=" + mytb_id);

        Document doc_liste = Jsoup.connect(mytb_url_liste).get();

        Element table_entry = doc_liste.selectFirst("body > table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(3) > table:nth-child(2) > tbody > tr.table_entry > td > table > tbody");
        
        if(table_entry == null) break;
        
        Elements tb_eintraege = table_entry.getElementsByTag("tr");   
        
        for (Element tb_eintrag : tb_eintraege) {
          if(j++ >= mytb_entries) break;
          Element datum = tb_eintrag.selectFirst("td:nth-child(1) > i");
          Element link = tb_eintrag.selectFirst("td:nth-child(2) > b > a");
          String linkHref = link.attr("href");
          String linkText = link.text();

          // EID aus der URL filtern
          Matcher m = eid_regex.matcher(linkHref);
          m.find();
          String eid = m.group();

          System.out.println(j +". " + datum.text() + " - " + linkText);

          Document doc_eintrag = Jsoup.connect(mytb_url_eintrag + eid).get();
          Element mytb_eintrag = doc_eintrag.select("td.listentextxl").first();   

          //System.out.println(mytb_eintrag.html());
          wxr.writeItem(linkText, wp_df.format((Date)df.parse(datum.text())), mytb_eintrag.html(), eid);                  

        }
        
        if(j >= mytb_entries) break;
      }
      
      wxr.writeExtro();
      
    } catch (IOException | ParseException | org.apache.commons.cli.ParseException e1 ) {
      e1.printStackTrace();
    } 
  }
}
