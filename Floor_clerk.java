import java.util.Random;
import java.util.concurrent.Semaphore;

public class Floor_clerk
    extends Thread {

    static Semaphore mtx_Flr_clerk_help_cust = new Semaphore(1);

    public Floor_clerk(String s){
	super(s);
    }

    void msg(String m) {
	System.out.println("["+(System.currentTimeMillis()-BALA.time)+"] Floor clerk "+getName()+": "+m);
    }

    public void run() {
	msg("showed up to work.");
	try {
	    while(true) {
		BALA.mtx_Done_browsing.acquire();
		if(no_more_customers()) {
		    BALA.mtx_Done_browsing.release();
		    break;
		} BALA.mtx_Done_browsing.release();
		help_cust();
	    }
	    go_home();
	} catch (InterruptedException e) { msg("was interrupted abnormally"); }
    }

    boolean no_more_customers() {
	msg(BALA.Done_browsing+" customers served.");
	return BALA.n_customers == BALA.Done_browsing;
    }

    void help_cust() throws InterruptedException {
	msg("is waiting for browsing customers to help.");
	mtx_Flr_clerk_help_cust.acquire(); 
	BALA.mtx_Done_browsing.acquire();
	if(no_more_customers()) {
	    BALA.mtx_Done_browsing.release();
	    mtx_Flr_clerk_help_cust.release();
	    return;
	} else {
	    BALA.mtx_Done_browsing.release();
	    BALA.sem_Cust_wait_flr_clrk.acquire();
	    mtx_Flr_clerk_help_cust.release();
	    msg("now helping next customer.");
	    Floor_clerk.sleep(new Random().nextInt(5_000));
	    BALA.sem_Flr_clrk_wait_cust.release();
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
