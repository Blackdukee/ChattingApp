/**
 * The FriendController class extends the functionality of UserController 
 * to manage friendships between User objects. It provides methods to 
 * add or delete friend relationships and handles database transactions 
 * via Hibernate.
 */
package org.example.controller;

import org.example.models.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * FriendController is responsible for handling operations that manage 
 * add and remove friend relationships between User entities. It utilizes 
 * Hibernate sessions and transactions to persist these changes to the database.
 */
public class FriendController extends UserController {

    /**
     * Adds a friendship between two users. It updates both the primary 
     * user's and the friend's friend lists, and commits the changes 
     * to the database.
     *
     * @param user   the user initiating the friendship
     * @param friend the user to be added as a friend
     */
    public static void  addFriend(User user, User friend) {

        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            user.getFriends().add(friend);
            friend.getFriends().add(user);

            updateUser(user);
            updateUser(friend);

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Removes a friendship relationship between two users. It updates both
     * the primary user's and the friend's friend lists by removing each one
     * from the other's list, then persists these changes to the database.
     *
     * @param user   the user initiating the friend removal
     * @param friend the user to be removed as a friend
     */
    public static void deleteFriend(User user, User friend) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            user.getFriends().remove(friend);
            friend.getFriends().remove(user);

            updateUser(user);
            updateUser(friend);

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}