package com.dvoss;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static HashMap<String, User> users = new HashMap();

    public static void main(String[] args) throws IOException {

        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    //users = readFile();

                    Session session = request.session();
                    String username = session.attribute("username");
                    HashMap m = new HashMap();
                    if (username == null) {
                        return new ModelAndView(m, "index.html");
                    }
                    else {
                        users.get(username);
                        User user = users.get(username);
                        int msgDelete = 1;
                        for (Message mess : user.messages) {
                            mess.msgDelete = msgDelete;
                            msgDelete++;
                        }
                        m.put("name", user.name);
                        m.put("messages", user.messages);
                    }
                    return new ModelAndView(m, "messages.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/create-user",
                (request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                    if (username == null || password == null) {
                        Spark.halt("Name or password not sent");
                    }
                    User user = users.get(username);
                    if (user == null) {
                        user = new User(username, password);
                        users.put(username, user);
                    }
                    else if (!password.equals(user.password)) {
                        Spark.halt("Wrong password");
                    }
                    Session session = request.session();
                    session.attribute("username", username);

                    //writeFile();

                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/create-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        throw new Exception("Not logged in");
                    }
                    String newMessage = request.queryParams("message");
                    if (newMessage == null) {
                        throw new Exception("Invalid form field");
                    }
                    User user = users.get(username);
                    if (user == null) {
                        throw new Exception("User does not exist");
                    }
                    Message msg = new Message(newMessage);
                    user.messages.add(msg);

                    //writeFile();

                    response.redirect("/");
                    return "";
                }

        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();

                    //writeFile();

                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/delete-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        throw new Exception("Not logged in");
                    }
                    int numberDelete = Integer.valueOf(request.queryParams("msgdelete"));
                    User user = users.get(username);
                    user.messages.remove(numberDelete - 1);

                    //writeFile();

                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/edit-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        throw new Exception("Not logged in");
                    }
                    int numberEdit = Integer.valueOf(request.queryParams("msg-edit-number"));
                    User user = users.get(username);
                    String msgEdited = request.queryParams("msg-edit");
                    Message msg = new Message(msgEdited);
                    user.messages.set(numberEdit - 1, msg);

                    //writeFile();

                    response.redirect("/");
                    return "";
                }
        );
    }
//    public static void writeFile() throws IOException {
//        File f = new File("messages.json");
//        JsonSerializer serializer = new JsonSerializer();
//        HashMap<String, User> tempMap = users;
//        String json = serializer.include("*").serialize(tempMap);
//        FileWriter fwJson = new FileWriter(f);
//        fwJson.write(json);
//        fwJson.close();
//    }
//
//    public static HashMap<String, User> readFile() throws FileNotFoundException {
//        File f = new File("messages.json");
//        Scanner scanner = new Scanner(f);
//        scanner.useDelimiter("\\Z");
//        String contents = scanner.next();
//        JsonParser parser = new JsonParser();
//        // must create arraylist of hashmaps, for:each to get user objects -- see ContactsDesktop
//        parser.parse(contents);
//        HashMap<String, User> tempUserMap = parser.parse(contents);
//        return tempUserMap;
//    }
}
