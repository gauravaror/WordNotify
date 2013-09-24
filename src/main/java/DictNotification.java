/*
 * Copyright (c) 2012, IDM
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *     * Neither the name of the IDM nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.Integer;
import org.gnome.gtk.Gtk;
import org.gnome.notify.Notify;
import org.gnome.notify.Notification;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;


import fr.idm.sk.publish.api.client.light.SkPublishAPI;

public class DictNotification {
    private static Random randomGenerator;
    private static ArrayList<String> words;
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("need baseurl and accesskey in parameters"+args.length);
            return;
        }

        DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
        SkPublishAPI api = new SkPublishAPI(args[0] + "/api/v1", args[1], httpClient);
        api.setRequestHandler(new SkPublishAPI.RequestHandler() {
            public void prepareGetRequest(HttpGet request) {
                //System.out.println(request.getURI());
                request.setHeader("Accept", "application/json");
            }
        });

    try {

        words = new ArrayList<String>();
        randomGenerator = new Random();
        //System.out.println("*** Dictionaries");
        JSONArray dictionaries = new JSONArray(api.getDictionaries());
        //System.out.println(dictionaries);
        JSONObject dict = dictionaries.getJSONObject(0);
        //System.out.println(dict);
        String dictCode = dict.getString("dictionaryCode");


        FileInputStream fstream = new FileInputStream("/home/gaurav/Words.txt");
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        Gtk.init(args);
        Notify.init("Words");

        while ((strLine = br.readLine()) != null)   {
            words.add(strLine);

        }
        //System.out.println("*** Words Loaded");
        while (words.size() > 0) {
            try {
            int index = randomGenerator.nextInt(words.size());
            String item = words.remove(index);
            Notification Hello = new Notification(item, "Looking for Meaning, it's for you to tell.", "dialog-information");
            Hello.show();
            //System.out.println("*** Notification Should Have come");
            Thread.sleep(Integer.parseInt(args[2])*1000);
            //System.out.println("*** Best matching");
            JSONObject bestMatch = new JSONObject(api.searchFirst(dictCode, item, "xml"));
            //System.out.println(bestMatch);
            String xml = bestMatch.getString("entryContent");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = builder.parse(is);
            NodeList nodeList = doc.getElementsByTagName("definition");
            String currentAction = "NOT-FOUND";
//            for (int s = 0; s < nodeList.getLength(); s++) {
                  Node fstNode = nodeList.item(0);
                  if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element fstElmnt = (Element) fstNode;

                        NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("def");
                        Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                        NodeList lstNm = lstNmElmnt.getChildNodes();
                        currentAction = ((Node) lstNm.item(0)).getNodeValue();
                        //System.out.println(currentAction);
                    }
  //              }
            Notification Meaning = new Notification(item, currentAction, "dialog-information");
            Meaning.show();
            Thread.sleep(Integer.parseInt(args[3])*60000);
            }
            catch (Exception e) {
                Thread.sleep(Integer.parseInt(args[3])*60000);
            }
        }

        }
        catch (Exception e) {
        e.printStackTrace();
    }
    }
}

