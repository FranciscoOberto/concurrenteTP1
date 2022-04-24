import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Random;

public class Buffer {

    private final int LimiteDatos;
    private HashMap<Integer, Dato> datos;
    private int rechazados;
    private final ReentrantReadWriteLock lock;

    /**
     * Constructor con parámetros
     * Inicializa las variables de instancia
     * @param LimiteDatos Cantidad máxima de datos.
     */
    public Buffer(int LimiteDatos) {
        this.LimiteDatos = LimiteDatos;
        this.datos = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.rechazados = 0;
    }

    /**
     * Le asigna un ID a un dato y lo agrega al Buffer.
     * No se puede agregar un dato si el total de datos es mayor
     * o igual a LimiteDatos.
     * @param dato El dato a agregar en el Buffer.
     */
    public void agregarDato(Dato dato) throws Exception {
        //Metemos todo dentro de un writeLock para que size no cambie hasta que se agrega el Dato.
        this.lock.writeLock().lock();
        if (datos.size() == LimiteDatos){
            this.rechazados++;
            this.lock.writeLock().unlock();
            return;
        }else if (datos.size() > LimiteDatos){
            this.lock.writeLock().unlock();
            throw new Exception("Buffer rebalsado Exception" + this.datos.size());
        }
        datos.put(dato.getId(), dato);
        this.lock.writeLock().unlock();
    }


    /**
     * Obtiene un dato del Buffer.
     * Si no hay datos o si están en uso, devuelve null.
     */
    public Dato obtenerDato() throws Exception{
        this.lock.readLock().lock();
        if (datos.isEmpty()) {
            this.lock.readLock().unlock();
            return null;
            //throw new Exception("Buffer vacío");
        }
        Random generator = new Random();
        //Object[] values = this.datos.values().toArray();
        Set keys = datos.keySet();
        int size = keys.size();
        int n = generator.nextInt(size);
        this.lock.readLock().unlock();
        System.out.printf("Dato devuelto id: %d\n", datos.get(n).getId());
        return datos.get(n);
        //return (Dato) values[generator.nextInt(values.length)];
    }

    /**
     * Elimina un dato del Buffer.
     * @param id El id del dato a eliminar del Buffer.
     */
    public void BorrarDato(int id) throws Exception {
        this.lock.readLock().lock();
        if (!datos.containsKey(id)){
            this.lock.readLock().unlock();
            throw new Exception("El dato ya fue borrado.");
        }
        this.lock.writeLock().lock();
        datos.remove(id);
        this.lock.writeLock().unlock();
    }

}
