package com.liferay.docs.guestbook.sheduler;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.docs.guestbook.model.GuestbookEntry;
import com.liferay.docs.guestbook.service.GuestbookEntryLocalServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(property = { "dispatch.task.executor.name=GuestbookEntryInsertion", "dispatch.task.executor.type=onInsert" }, service = DispatchTaskExecutor.class)

public class GuestBookEntryTaskExecutor extends BaseDispatchTaskExecutor {
	
	private static Log  _log= LogFactoryUtil.getLog(GuestBookEntryTaskExecutor.class);

	@Override
	public String getName() {
		
		return "GuestbookEntryInsertion";
	}

	@Override
	public void doExecute(DispatchTrigger dispatchTrigger, DispatchTaskExecutorOutput dispatchTaskExecutorOutput) {
		
		_log.info("Executing scheduler for inserting new guestbook entries");
		
        insertGuestbookEntries();
		
	}
	
	private void insertGuestbookEntries() {
		
        List<GuestbookEntry> entries = generateGuestbookEntries();

        for (GuestbookEntry entry : entries) {
        	
            GuestbookEntryLocalServiceUtil.addGuestbookEntry(entry);
            
            _log.info("Inserted guestbook entry: " + entry.getEntryId());
        }

        _log.info(entries.size() + " entries inserted into the GuestbookEntry table. Log from Message Listener.");
    }
	
	 private List<GuestbookEntry> generateGuestbookEntries() {
		 
	        List<GuestbookEntry> entries = new ArrayList<>();
	        
	        GuestbookEntry entry1 = GuestbookEntryLocalServiceUtil.createGuestbookEntry(CounterLocalServiceUtil.increment());
	        entry1.setName("Jonny");
	        entry1.setEmail("jj@liferay.com");
	        entry1.setMessage("Hello, world!");
	        entry1.setCreateDate(new Date());
	        entries.add(entry1);
	        
	        return entries;
	    }
}
