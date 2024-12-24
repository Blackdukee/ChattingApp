package org.example.controller;

import org.example.models.Message;
import org.example.models.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.List;

/**
 * MessageController handles the operations related to messages
 * such as saving, updating, deleting messages, and retrieving messages.
 */
public class MessageController {

    private static Transaction transaction = null;
    private static Session session = null;

    /**
     * Saves a message to the database with the current timestamp.
     *
     * @param message the message to be saved
     */
    public static void saveMessage(Message message) {
        try {

            Date currentDate = new Date();
            message.setTimestamp(currentDate);
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(message);
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
     * Updates an existing message in the database.
     *
     * @param message the message to be updated
     */
    public static void updateMessage(Message message) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.merge(message);
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
     * Deletes a message from the database.
     *
     * @param message the message to be deleted
     */
    public static void deleteMessage(Message message) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.remove(message);
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
     * Retrieves the count of unread messages from a specific sender to a specific recipient.
     *
     * @param sender the username of the sender
     * @param recipient the username of the recipient
     * @return the count of unread messages
     */
    public static int getUnreadMessagesCount(String sender, String recipient) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            User user = UserController.getUserByUsername(sender);
            User friend = UserController.getUserByUsername(recipient);
            int count = session.createQuery("from Message where sender = :sender and recipient = :recipient and isRead = false", Message.class)
                    .setParameter("sender", friend)
                    .setParameter("recipient", user)
                    .getResultList().size();
            transaction.commit();
            return count;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return 0;
        } finally {
            session.close();
        }
    }

    /**
     * Retrieves a list of messages between two users.
     *
     * @param sender the ID of the sender
     * @param recipient the ID of the recipient
     * @return a list of messages between the two users
     */
    public static List<Message> getMessages(long sender, long recipient) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            List<Message> messages = session.createQuery("from Message where sender.id = :sender and recipient.id = :recipient or sender.id = :recipient and recipient.id = :sender", Message.class)
                    .setParameter("sender", sender)
                    .setParameter("recipient", recipient)
                    .getResultList();
            List<Message> messages2 = session.createQuery("from Message where sender.id = :recipient and recipient.id = :sender", Message.class)
                    .setParameter("sender", sender)
                    .setParameter("recipient", recipient)
                    .getResultList();

            messages.addAll(messages2);

            messages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

            transaction.commit();
            return messages;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }
}
