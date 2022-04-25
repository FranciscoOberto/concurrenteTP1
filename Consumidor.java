import java.util.concurrent.TimeUnit;

public class Consumidor implements Runnable {

    private static int totalConsumidos = 0;
    private final int demoraConsumidor;
    private final Buffer bufferValidado;
    private final Buffer bufferInicial;
    private int cantidadConsumidos;
    private static final int MAXIMAS_CONSUMISIONES = 1000;
    private final Object controlConsumisiones;

    /** Constructor con parametros  */
    public Consumidor(Buffer bufferValidado, Buffer bufferInicial,int demoraConsumidor) {
        this.demoraConsumidor = demoraConsumidor;
        this.bufferInicial = bufferInicial;
        this.bufferValidado = bufferValidado;
        cantidadConsumidos =  0;
        controlConsumisiones = new Object();
    }

    public synchronized void aumentarConsumisiones() {
        synchronized (controlConsumisiones) {
            totalConsumidos++;
        }
    }

    public static synchronized int getTotalConsumidos(){
        return totalConsumidos;
    }

    public static int getMaximasConsumisiones(){
        return MAXIMAS_CONSUMISIONES;
    }

    public void consumir(){
        if (this.bufferValidado.estaVacio())
            return;
        try {
            TimeUnit.SECONDS.sleep(this.demoraConsumidor);
            boolean consumido = bufferValidado.consumirDato();
            if (consumido) {
                cantidadConsumidos++;
                aumentarConsumisiones();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void run(){
        do {
            consumir();
        } while (bufferValidado.getConsumidos() != MAXIMAS_CONSUMISIONES);
        //System.out.println("Consumidor: consumidos = " + cantidadConsumidos + " Total consumidos = "+ getTotalConsumidos());
        System.out.println("Consumidor: consumidos = " + cantidadConsumidos);
    }

}
