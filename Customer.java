import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class Customer
    extends Thread {

    static Semaphore mtx_Cust_wait_help_flr = new Semaphore(1);
    static Semaphore mtx_Cust_getting_helped = new Semaphore(1);
    static Semaphore mtx_Cust_getting_item = new Semaphore(1);
    static Semaphore mtx_Customers_wait_to_leave = new Semaphore(0);
    
    Item my_item;

    public Customer(String s) {
	super(s);
    }

    void msg(String m) {
	System.out.println("["+(System.currentTimeMillis()-BALA.time)+"] Customer "+getName()+": "+m);
    }

    public void run() {
	try {
	    browse();
	    get_slip();
	    pay();
	    leave_BALA();
	} catch (Exception e) { msg("was abnormally interrupted."); }
    }

    void browse() throws InterruptedException {
	msg("is browsing for items in BALA.");
	Thread.sleep(2000);
	my_item = new Item();
	msg("chooses a "+my_item.weight()+" item.");
    }

    void get_slip() throws InterruptedException {
	BALA.sem_Cust_wait_flr_clrk.release();
	mtx_Cust_wait_help_flr.acquire(); 
	msg("waiting on line for floor clerk.");
	BALA.sem_Flr_clrk_wait_cust.acquire(); 
	mtx_Cust_wait_help_flr.release();
	msg("getting slip from floor clerk.");
	BALA.mtx_Done_browsing.acquire(); 
	BALA.Done_browsing++;
	BALA.mtx_Done_browsing.release();
    }
    
    void pay() throws InterruptedException {
	msg("gets on line to pay.");
	Customer.sleep(new Random().nextInt(10_000));
	msg("pays for the "+my_item.weight+" item.");
	if(!my_item.weight().equalsIgnoreCase("light")) wait_receive();
    }

    void wait_receive() throws InterruptedException {
	msg("arrives at the storage room to receive their item.");
	Thread.sleep(2000);
	get_helped();
    }

    void get_helped() throws InterruptedException {
	mtx_Cust_getting_helped.acquire(); 
	for(int i = 0; i < 3; i++) {
	    BALA.sem_Cust_wait_stor_clrk.release();
	    msg("waiting for "+(3-i)+" clerks.");
	    BALA.sem_Stor_clrk_wait_cust.acquire();
	} 
	msg("being helped by a clerk.");
	mtx_Cust_getting_helped.release();
	receive_item();
    }

    void receive_item() throws InterruptedException {
	mtx_Cust_getting_item.acquire(); 
	for(int i = 0; i < 3; i++) {
	    BALA.sem_Item_from_storage.acquire(); 
	}
	msg("has received their "+my_item.weight()+" item.");
	mtx_Cust_getting_item.release();
    }
    
    void leave_BALA() throws InterruptedException {
	msg("browses around BALA until closing time.");
	BALA.mtx_Finished_shopping.acquire(); 
	BALA.Finished_shopping++;
	if(BALA.Finished_shopping == BALA.n_customers) {
	    BALA.mtx_Clerks_wait_to_close_up.release();
	    mtx_Customers_wait_to_leave.release();
	    msg("shouts \"it's closing time!\".");
	}
	BALA.mtx_Finished_shopping.release();
	mtx_Customers_wait_to_leave.acquire();
	Customer.sleep(1000);
	mtx_Customers_wait_to_leave.release();
	msg("leaves BALA.");
    }
}
