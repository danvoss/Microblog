package com.dvoss;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    HashMap m = new HashMap();
                    if (username == null) {
                        return new ModelAndView(m, "index.html");
                    }
                    else {
                        User user = users.get(username);
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
                        //throw new Exception("Name or password not sent.");
                        Spark.halt("Name or password not sent");
                    }
                    User user = users.get(username);
                    if (user == null) {
                        user = new User(username, password);
                        users.put(username, user);
                    }
                    else if (!password.equals(user.password)) {
                        //throw new Exception("Wrong password");
                        Spark.halt("Wrong password");
                    }
                    Session session = request.session();
                    session.attribute("username", username);

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
//                        Spark.halt("Not logged in");
                    }
                    String newMessage = request.queryParams("message");
                    if (newMessage == null) {
                        throw new Exception("Invalid form field");
//                        Spark.halt("Invalid form field");
                    }
                    User user = users.get(username);
                    if (user == null) {
                        throw new Exception("User does not exist");
//                        Spark.halt("User does not exist");
                    }
                    Message msg = new Message(newMessage);
                    user.messages.add(msg);
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
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
                    int numberDelete = Integer.valueOf(request.queryParams("msg-delete"));
                    User user = users.get(username);
                    user.messages.remove(numberDelete - 1);
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

                    // how to change existing message to new (request.queryParams("msg-edit")) ??


                    response.redirect("messags.html");
                    return "";
                }
        );
    }
}
