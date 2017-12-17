package MyTBCrawler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.bican.wordpress.Post;
import net.bican.wordpress.Wordpress;
import net.bican.wordpress.exceptions.InsufficientRightsException;
import net.bican.wordpress.exceptions.InvalidArgumentsException;
import net.bican.wordpress.exceptions.ObjectNotFoundException;
import redstone.xmlrpc.XmlRpcFault;

public class MyTBCrawler {

  public static void main(String[] args) {
    
    // Wordpress Benutzername
    String wp_username = new String("midlifecrisis74");
    
    // Euer Wordpress Passwort :-)
    String wp_password = new String("sag-ich-euch-nicht");
    
    // URL zu Eurem Wordpress-Blog - das xmlrpc.php am Ende ist wichtig!
    String wp_url = new String("https://privatemlc74.wordpress.com/xmlrpc.php");
    
    // myTagebuch Autoren id
    Integer mytb_id = new Integer(16493);
    
    // wieviele oeffentliche Tagebucheintraege sind im Tagebuch?
    Integer mytb_entries = new Integer(625-25);
    
    // myTagebuch URL fuer die Liste der Tagebucheintraege (wir beginnen bei den aeltesten)
    String mytb_url_liste = new String("http://www.mytagebuch.de/profil.php?action=alleeintraege&offset=" 
                                       + mytb_entries 
                                       + "&id=" + mytb_id);
    
    // myTagebuch URL-Teil fuer den Abruf von Tagebucheintraegen (die EID am Ende wird weiter unten in der Schleife angefuegt)
    String mytb_url_eintrag = new String("http://www.mytagebuch.de/profil.php?action=eintrag&id=" + mytb_id
                                         + "&eid=");
    
    // Regular Expression um die eid aus den URLs der Liste zu filtern
    Pattern eid_regex = Pattern.compile("(\\d+)$");
    
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    try {
      Document doc_liste = Jsoup.connect(mytb_url_liste).get();
      Wordpress wp = new Wordpress(wp_username, wp_password, wp_url);

      Element table_entry = doc_liste.selectFirst("body > table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(3) > table:nth-child(2) > tbody > tr.table_entry > td > table > tbody");
      Elements tb_eintraege = table_entry.getElementsByTag("tr");      
      for (Element tb_eintrag : tb_eintraege) {
        Element datum = tb_eintrag.selectFirst("td:nth-child(1) > i");
        Element link = tb_eintrag.selectFirst("td:nth-child(2) > b > a");
        String linkHref = link.attr("href");
        String linkText = link.text();
        
        // EID aus der URL filtern
        Matcher m = eid_regex.matcher(linkHref);
        m.find();
        String eid = m.group();
        
        System.out.println("===============================================================================================");
        System.out.println("Eintragstitel: " + linkText);
        System.out.println("Eintragsdatum: " + datum.text());
        System.out.println("Eintragsurl: " + mytb_url_eintrag + eid);

        Document doc_eintrag = Jsoup.connect(mytb_url_eintrag + eid).get();
        Element mytb_eintrag = doc_eintrag.select("td.listentextxl").first();   
        
        //System.out.println(mytb_eintrag.html());

        Post wp_tb_eintrag = new Post();
        wp_tb_eintrag.setPost_title(linkText);
        wp_tb_eintrag.setPost_date((Date)df.parse(datum.text()));
        wp_tb_eintrag.setPost_content(mytb_eintrag.html());
        wp_tb_eintrag.setPost_status("publish");
        Integer result;
        result = wp.newPost(wp_tb_eintrag);

        System.out.println("===============================================================================================");        
      }
    } catch (IOException | InsufficientRightsException | InvalidArgumentsException | ObjectNotFoundException | XmlRpcFault | ParseException e1 ) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }    
  }
}
