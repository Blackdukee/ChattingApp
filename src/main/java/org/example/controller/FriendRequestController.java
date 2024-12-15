package org.example.controller;

import org.example.models.FriendRequest;
import org.example.models.Status;
import org.example.models.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestController {

    public static void sendFriendRequest(FriendRequest friendRequest) throws Exception {
        Transaction transaction = null;
        Session session = null;
        try {


            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();



            List<FriendRequest> f =   session.createQuery("from FriendRequest where sender = :sender and recipient = :recipient", FriendRequest.class)
                    .setParameter("sender", friendRequest.getSender())
                    .setParameter("recipient", friendRequest.getRecipient())
                    .stream().toList();
            if(!f.isEmpty()){
                throw new Exception("Friend request already sent");
            }

            session.persist(friendRequest);

            transaction.commit();
        } catch (Exception e) {
            if (e.getMessage().equals("Friend request already sent")) {
                throw new Exception("Friend request already sent");
            }
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

    public static void deleteFriendRequest(FriendRequest friendRequest) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.remove(friendRequest);

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

    public static List<String> getFriendRequests(User user) {
        List<String> friendRequestNames = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<FriendRequest>  friendRequests = session.createQuery("from FriendRequest where recipient = :user", FriendRequest.class)
                    .setParameter("user", user)
                    .list();


            friendRequestNames = new ArrayList<>();
            for (FriendRequest friendRequest : friendRequests) {
                friendRequestNames.add(friendRequest.getSender().getUsername());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendRequestNames;
    }


    public static void acceptFriendRequest(FriendRequest friendRequest) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            if (friendRequest.getIsAccepted() == Status.PENDING) {
                friendRequest.setAccepted();
            }

            User user = friendRequest.getSender();
            User friend = friendRequest.getRecipient();
            FriendController.addFriend(user, friend);

            session.remove(friendRequest);

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

    public static FriendRequest getFriendRequest(User sender, User recipient) {
        FriendRequest friendRequest = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            friendRequest = session.createQuery("from FriendRequest where sender = :sender and recipient = :recipient", FriendRequest.class)
                    .setParameter("sender", sender)
                    .setParameter("recipient", recipient)
                    .uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendRequest;
    }
}