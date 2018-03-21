import java.util.Random;
import java.util.concurrent.Semaphore;

public class Storage_clerk
    extends Thread {

    Semaphore mtx_Stor_clrk_serve_cust = new Semaphore(1);

    public Storage_clerk(String s) {
	super(s);
    }

    void msg(String m) {
	System.out.println("["+(System.currentTimeMillis()-BALA.time)+"] Storage clerk "+getName()+": "+m);
    }

    public void run() {
	msg("showed up to work.");
	try {
	    while(true) {
		BALA.mtx_Finished_shopping.acquire();
		if(no_more_customers()) {
		    BALA.mtx_Finished_shopping.release();
		    break;
		} BALA.mtx_Finished_shopping.release();
		help_cust();
	    } 
	    go_home();
	} catch (InterruptedException e) { msg("was interrupted abnormally"); }
    }

    boolean no_more_customers() {
	msg(BALA.Finished_shopping+" customers finished.");
	return BALA.n_customers == BALA.Finished_shopping;
    }

    void help_cust() throws InterruptedException {
	msg("now waiting on the next customer.");
	BALA.sem_Stor_clrk_wait_cust.release();
	mtx_Stor_clrk_serve_cust.acquire(); 
	BALA.mtx_Finished_shopping.acquire();
	if(no_more_customers()) {
	    BALA.mtx_Finished_shopping.release();
	    mtx_Stor_clrk_serve_cust.release();
	    return;
       	} else {
	    BALA.mtx_Finished_shopping.release();
	    BALA.sem_Cust_wait_stor_clrk.acquire();
	    mtx_Stor_clrk_serve_cust.release();
	    msg("goes into the back to get the item.");
	    Storage_clerk.sleep(new Random().nextInt(10_000));
	    msg("comes back with the item.");
	    BALA.sem_Item_from_storage.release();
	}
    }

    void go_home() throws InterruptedException {
	msg("has no more customers waiting.");
	BALA.mtx_Clerks_wait_to_close_up.acquire();
	BALA.mtx_Clerks_wait_to_close_up.release();
	msg("cleans up and gets ready to close BALA.");
	Floor_clerk.sleep(new Random().nextInt(5_000));
	BALA.mtx_Clock_out.acquire();
	BALA.Clerks_clocked_out++;
	if(BALA.Clerks_clocked_out == 1)
	    msg("Good night! Last one out - please lock the doors.");
	if(BALA.Clerks_clocked_out == BALA.n_floor_clerks + BALA.n_storage_clerks)
	    msg("is the last clerk to leave BALA. The day ends.");
	BALA.mtx_Clock_out.release();
	msg("leaves BALA.");
    } 
}
