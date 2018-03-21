import java.util.concurrent.Semaphore;

public class BALA {

    static int n_customers;
    static int n_floor_clerks;
    static int n_storage_clerks;

    static Semaphore sem_Flr_clrk_wait_cust = new Semaphore(0);
    static Semaphore sem_Cust_wait_flr_clrk = new Semaphore(0);
    static Semaphore mtx_Done_browsing = new Semaphore(1);
    static int Done_browsing = 0;
    
    static Semaphore sem_Cust_wait_stor_clrk = new Semaphore(0);
    static Semaphore sem_Stor_clrk_wait_cust = new Semaphore(0);
    static Semaphore sem_Item_from_storage = new Semaphore(0);

    static Semaphore mtx_Finished_shopping = new Semaphore(1);
    static int Finished_shopping = 0;

    static Semaphore mtx_Clerks_wait_to_close_up = new Semaphore(0);
    static Semaphore mtx_Clock_out = new Semaphore(1);
    static int Clerks_clocked_out = 0;
    
    static long time = System.currentTimeMillis();
    static final long LONG_TIME = 999_999_999;

    public static void main(String[] args) {


	if(args.length < 1) {
	    n_customers = 10;
	    n_floor_clerks = 2;
	    n_storage_clerks = 6;
	} else if(args.length < 2) {
	    n_floor_clerks = 2;
	    n_storage_clerks = 6;
	} else if(args.length < 3) {
	    n_storage_clerks = 6;
	} else {
	    n_customers = Integer.parseInt(args[0]);
	    n_floor_clerks = Integer.parseInt(args[1]);
	    n_storage_clerks = Integer.parseInt(args[2]);
	}

	for(int i = 0; i < n_floor_clerks; i++) {
	    new Floor_clerk("FC"+String.valueOf(i)).start();
	}
	for(int i = 0; i < n_storage_clerks; i++) {
	    new Storage_clerk("SC"+String.valueOf(i)).start();
	}
	for(int i = 0; i < n_customers; i++) {
	    new Customer("C"+String.valueOf(i)).start();
	}
    }
}
