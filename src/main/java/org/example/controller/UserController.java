package org.example.controller;

import org.example.models.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserController {
    private static Transaction transaction = null;
    private static Session session = null;

    public static void saveUser(User user) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            if (e.getMessage().contains("UNIQUE KEY")) {
                System.out.println("User already exists");
            } else {
                e.printStackTrace();
            }
        } finally {

            session.close();
        }
    }

    public static void updateUser(User user) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void deleteUser(User user) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.remove(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }



    public static User getUserByUsername(String username) {
        User user = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            user = session.createQuery("from User where username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return user;
    }

    public static User Loing(String username, String password) {
        User user = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            user = session.createQuery("from User where username = :username and password = :password", User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return user;
    }


    public static ConcurrentHashMap<String, Boolean> getFriends(String username) {
        ConcurrentHashMap<String,Boolean> friends = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
//            User user = session.createQuery("from User where username = :username", User.class)
//                    .setParameter("username", username)
//                    .uniqueResult();
            // get user by username

            // how to get user by its attribute

            User user = session.createQuery("from User where username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
            if (user == null) {
                return null;
            }
            if (user.getFriends() != null ) {
                friends = (ConcurrentHashMap<String, Boolean>) user.getFriends().stream().collect(Collectors.toConcurrentMap(User::getUsername, User::isActive));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return friends;
    }


    public static void setUserStatus(String username, boolean status) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            User user = session.createQuery("from User where username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (user == null) {
                return;
            }
            user.setActive(status);
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
