package web_tools;

import java.io.*;
import java.net.Socket;

/**
 * A proxy class that can be used to send and receive messages.
 * The proxy class of the transmission.
 * @see TransmissionListener
 * @see Sender
 * @see Receiver
 * @see Serializable
 * @see ObjectOutputStream
 * @version beta1.1
 * @see Closeable
 */
public class TransmissionController implements Closeable{
    /**
     * An instance of TransmissionListener.
     * To make static proxy to use.
     */
    private TransmissionListener listener;
    private Sender sender;
    private Receiver receiver;
    private Socket targetSocket;
    private Thread receivingThread;

    /**
     * Constructor of the transmission proxy <code>TransmissionController</code>
     * @param targetSocket The socket your program plugged
     * @param listener An instance you want to present.
     * @see TransmissionListener

     * @throws NullPointerException If the parameters you pass are all non-initialized variables, the exception will appear.
     * @throws IOException If there exists IOException in the input or output stream of the parameter <code>serverSocket</code>,
     * this exception will appear.
     * @version beta1.0
     */
    public TransmissionController(Socket targetSocket, TransmissionListener listener) throws IOException, NullPointerException{
        if(targetSocket == null) throw new NullPointerException("The socket has not been initialized");
        if(listener == null) throw new NullPointerException("The listener has not been initialized");

        this.targetSocket = targetSocket;
        this.listener = listener;

        this.sender = new Sender(this.targetSocket.getOutputStream(), listener);
        this.receiver = new Receiver(this.targetSocket.getInputStream(), listener);

        this.receivingThread = new Thread(this.receiver);
        receivingThread.start();

        listener.onTransmissionStart();
    }

    /**
     * Send the message to the socket it connect to.
     * @param data <b>Should be serialised!</b>
     *                The message you want to send.

     * @param <T> The class type of the message you want to send. Use template to make the class more flexible and easy to use.
     

     * @see Serializable

     * @version beta1.0
     *
     */
    public <T extends Serializable> void send(T data){
        if(data == null){
            //listener.alertError("NULL-Pointer");
            listener.onTransmissionError("NULL-Pointer", TransmissionListener.ErrorType.NULL_POINTER);
            return;
        }
        new Thread(()->sender.send(data)).start();
    }

    /**
     *
     * @throws IOException When exception happens on closing the instances of receiver and sender,
     * the exception will appear.
     */
    @Override
    public void close() throws IOException {
        receiver.close();
        sender.close();
    }
}

/**
 * A class that can be used to send messages, as an inner class of <code>TransmissionController</code>.
 * @see TransmissionController
 * @see Serializable
 * @see ObjectOutputStream
 * @version beta1.1
 */
class Sender implements Closeable{
    private ObjectOutputStream outputStream;
    private TransmissionListener listener;
    boolean flag;

    /**
     * Constructor of the class <code>Sender</code>.
     */
    Sender(OutputStream outputStream, TransmissionListener listener) {
        this.listener = listener;
        try {
            this.outputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            listener.alertError("OutputInitError");
        }
        flag = true;
    }

    /**
     * Send the data to the server.
     * This is the <b>key method</b> of class <code>Sender</code>, even the whole class <code>TransmissionController</code>.
     * Use <code>synchronized</code> to ensure each thread can write the data to the stream one by one.
     * @param data The data you want to send. Use template to make the class more flexible and easy to use.
     * @param <T> The class type of the data you want to send.
     * @version beta1.1
     * @see Serializable
     * @see ObjectOutputStream
     * @see TransmissionController
     * @see TransmissionListener
     */
    <T extends Serializable> void send(T data){
        //Ensure each thread can write the data to the stream one by one.
        synchronized (this) {
            if(!flag) {
                listener.onTransmissionStart();
                //listener.alertError("WrongOutputStream");
                listener.onTransmissionError("WrongOutputStream", TransmissionListener.ErrorType.WRONG_OUTPUT_STREAM);
                return;
            }
            try {
                outputStream.writeObject(data);
            }
            catch (IOException e) {
                e.printStackTrace();
                listener.onTransmissionError("WrongIO", TransmissionListener.ErrorType.IO_EXCEPTION);
                flag = false;
            }
        }
    }

    /**
     * Close the output stream.
     * @throws IOException When exception happens on closing the output stream,
     * the exception will appear.
     */
    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }
}

/**
 * A class that can be used to receive messages, as an inner class of <code>TransmissionController</code>.
 * When a message comes, it will call the method <code>onTransmissionProgress</code>
 * in the interface <code>TransmissionListener</code> automatically.
 * @param <T> The class type of the message you want to receive.
 *           Remember to <b>keep the type the same as</b> you pass in <code>void send(T data)</code>
 *           in class <code>TransmissionController</code>.
 * @version beta1.1
 * @see TransmissionController
 * @see TransmissionListener
 * @see Serializable
 * @see ObjectInputStream
 */
class Receiver<T extends Serializable> implements Runnable, Closeable{
    private Socket socket;
    private TransmissionListener listener;
    private ObjectInputStream objectInputStream;
    boolean flag;


    Receiver(InputStream inputStream, TransmissionListener listener) throws IOException {
        try {
            this.objectInputStream = new ObjectInputStream(new BufferedInputStream(inputStream));
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("pass");
        this.listener = listener;
        flag = true;
        System.out.println("Receiver init over");
    }
    @Override
    public void run(){
        while(flag){
            try {
                T o = (T)objectInputStream.readObject();
                listener.onTransmissionProgress(o);
                listener.onTransmissionEnd();
            }catch (IOException e){
                e.printStackTrace();
                //listener.alertError("ReadError!");
                listener.onTransmissionError("WrongRead", TransmissionListener.ErrorType.WRONG_INPUT_STREAM);
                WebUtil.closeAll(objectInputStream);
                flag = false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                //listener.alertError("ClassNotFound");
                listener.onTransmissionError("ClassNotFound", TransmissionListener.ErrorType.CLASS_NOT_FOUND);
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.objectInputStream.close();
    }
}

