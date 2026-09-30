package project.advvvvvvvvvvvv;

import java.io.*;
import java.net.*;
import java.util.*;

public class HotelServer {
    private static Set<ObjectOutputStream> clientStreams = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Hotel Server is running on port 5000...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New employee connected: " + socket.getInetAddress());
                
                // تشغيل خيط لكل موظف
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        // التعديل هنا: نفتح الـ Output قبل الـ Input ونعمل flush
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // إرسال الـ Header فوراً عشان الطرف التاني ميهنجش
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            synchronized (clientStreams) {
                clientStreams.add(out);
            }

            Object input;
            while ((input = in.readObject()) != null) {
                System.out.println("Received: " + input);
                broadcast(input);
            }
        } catch (EOFException e) {
            System.out.println("Employee logged out.");
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    private static void broadcast(Object obj) {
        synchronized (clientStreams) {
            Iterator<ObjectOutputStream> it = clientStreams.iterator();
            while (it.hasNext()) {
                ObjectOutputStream out = it.next();
                try {
                    out.writeObject(obj);
                    out.reset(); // مهم جداً عشان يبعت الكائن المتعدل مش القديم
                    out.flush();
                } catch (IOException e) {
                    it.remove(); // مسح الموظف لو قفل البرنامج
                }
            }
        }
    }
}