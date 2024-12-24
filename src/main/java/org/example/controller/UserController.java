package org.example.controller;

import org.example.models.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * UserController handles the operations related to user management
 * such as signup, update, delete, and retrieving user information.
 */
public class UserController {
    private static Transaction transaction = null;
    private static Session session = null;

    /**
     * Signs up a new user by saving their information to the database.
     *
     * @param user the user to be signed up
     */
    public static void signup(User user) {
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

    /**
     * Updates an existing user's information in the database.
     *
     * @param user the user to be updated
     */
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

    /**
     * Deletes a user from the database.
     *
     * @param user the user to be deleted
     */
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

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to be retrieved
     * @return the user if found, otherwise null
     */
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

    /**
     * Logs in a user by checking their username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the user if the credentials match, otherwise null
     */
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

    /**
     * Retrieves a list of friends for a given user.
     *
     * @param username the username of the user whose friends are to be retrieved
     * @return a ConcurrentHashMap of friends' usernames and their active status
     * @throws Exception if the user is not found
     */
    public static ConcurrentHashMap<String, Boolean> getFriends(String username) throws Exception {
        ConcurrentHashMap<String,Boolean> friends = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            User user = session.createQuery("from User where username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
            if (user == null) {
               throw new Exception("User not found");
            }
            if (user.getFriends() != null ) {
                friends = (ConcurrentHashMap<String, Boolean>) user.getFriends().stream().collect(Collectors.toConcurrentMap(User::getUsername, User::isActive));
            }else {
                friends = new ConcurrentHashMap<>();
            }

        } catch (Exception e) {

            if (e.getMessage().equals("User not found")) {
                throw new Exception("User not found");
            } else {
                e.printStackTrace();
            }
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
        return friends;
    }

    /**
     * Sets the active status of a user.
     *
     * @param username the username of the user
     * @param status the active status to be set
     */
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
