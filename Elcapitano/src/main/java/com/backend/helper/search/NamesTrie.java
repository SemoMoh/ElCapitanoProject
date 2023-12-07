package com.backend.helper.search;

import java.util.*;

public class NamesTrie {
    private final NameNode root;

    public NamesTrie() {
        root = new NameNode();
    }

    public void addReservation(Reservation reservation) {
        String name = reservation.getName();
        char[] nameChar = name.toCharArray();
        NameNode current;
        NameNode nextNode = this.root;
        for (char currChar : nameChar) {
            current = nextNode;
            nextNode = current.getChild(currChar);
            if (nextNode == null) {
                nextNode = current.insert(currChar);
            }
        }
        nextNode.addReservation(reservation);
    }

    public List<Reservation> predictCompletions(String prefix, int numCompletions) {
        List<Reservation> result = new ArrayList<>(numCompletions);
        NameNode stemNode = this.root;
        char[] charArray = prefix.toCharArray();
        if (numCompletions == 0) {
            return result;
        }
        for (char currChar : charArray) {
            stemNode = stemNode.getChild(currChar);
            if (stemNode == null) {
                return result;
            }
        }
        Queue<NameNode> queue = new LinkedList<>();
        queue.add(stemNode);
        while (!queue.isEmpty() && result.size() < numCompletions) {
            NameNode currNode = queue.remove();
            if (currNode != null) {
                if (currNode.getReservations().size() > 0) {
                    result.addAll(currNode.getReservations());
                }
                Set<Character> validNextCharacters = currNode.getValidNextCharacters();
                for (char c : validNextCharacters) {
                    queue.add(currNode.getChild(c));
                }
            }
        }
        return result;
    }
}

class NameNode {
    private final HashMap<Character, NameNode> children;
    private String name;
    private boolean isName;
    private final List<Reservation> reservations;

    public NameNode() {
        children = new HashMap<>();
        reservations = new LinkedList<>();
    }

    public NameNode(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isName() {
        return isName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(boolean name) {
        isName = name;
    }

    public NameNode getChild(Character c) {
        return children.get(c);
    }

    public NameNode insert(Character c) {
        NameNode next = new NameNode(this.name + c);
        this.children.put(c, next);
        return next;
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public List<Reservation> getReservations() {
        return this.reservations;
    }

    public Set<Character> getValidNextCharacters() {
        return this.children.keySet();
    }

    @Override
    public String toString() {
        return "List: " + this.getReservations();
    }
}
