package com.liferay.docs.guestbook.sheduler;


import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.docs.guestbook.model.GuestbookEntry;
import com.liferay.docs.guestbook.service.GuestbookEntryLocalServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerEntryImpl;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.StorageTypeAware;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = {}, service = BaseMessageListener.class)

public class GuestBookEntryMessageListener extends BaseMessageListener {
	
	private static String groupNameString;
	private volatile boolean _initialized;
	private SchedulerEntryImpl _schedulerEntryImpl = null;
	private static final String CRON_EXPRESSION = "0 0/1 * * * ? *";
	private static Log _log = LogFactoryUtil.getLog(GuestBookEntryMessageListener.class);

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws SchedulerException {
		
		String cronExpresion = CRON_EXPRESSION;

		String listenerClass = getClass().getName();
		
		Trigger jobTrigger = _triggerFactory.createTrigger(listenerClass, listenerClass, new Date(), null, cronExpresion);
		
		_schedulerEntryImpl = new SchedulerEntryImpl(getClass().getName(), jobTrigger);
		
		if (_initialized) {
			
			deactivate();
		}

		_schedulerEngineHelper.register(this, _schedulerEntryImpl, DestinationNames.SCHEDULER_DISPATCH);
		
		groupNameString = GuestBookEntryMessageListener.class.getName();
		
		_log.debug("Scheduled task registered: " + cronExpresion);
		
		_initialized = true;

	}

	@Deactivate
	protected void deactivate() {
		unregister();
	}

	private void unregister() {

		if (_initialized) {

			try {
				
				_schedulerEngineHelper.unschedule(GuestBookEntryMessageListener.class.getName(), groupNameString, getStorageType());
				
			} catch (SchedulerException se) {
				
				if (_log.isWarnEnabled()) {
					
					_log.warn("Unable to unschedule trigger", se);
				}
			}

			_schedulerEngineHelper.unregister(this);
		}

		_initialized = false;
	}

	protected StorageType getStorageType() {
		
		if (_schedulerEntryImpl instanceof StorageTypeAware) {
			
			return ((StorageTypeAware) _schedulerEntryImpl).getStorageType();
		}
		
		return StorageType.MEMORY_CLUSTERED;
	}

	@Override
	protected void doReceive(Message message) throws Exception {

		_log.info("Executing scheduler");
		
		 insertGuestbookEntries();
	}
	
	private void insertGuestbookEntries() {
        List<GuestbookEntry> entries = generateGuestbookEntries();

        for (GuestbookEntry entry : entries) {
        	
            GuestbookEntryLocalServiceUtil.addGuestbookEntry(entry);
            
            _log.info("Inserted entry: " + entry.getEntryId());
        }

        _log.info(entries.size() + " entries inserted into the GuestbookEntry table. Log from Message Listener. Scheduler GroupName is " + groupNameString + ".");
    }
	
	 private List<GuestbookEntry> generateGuestbookEntries() {
		 
	        List<GuestbookEntry> entries = new ArrayList<>();
	        
	        GuestbookEntry entry1 = GuestbookEntryLocalServiceUtil.createGuestbookEntry(CounterLocalServiceUtil.increment());
	        entry1.setName("John");
	        entry1.setEmail("john@liferay.com");
	        entry1.setMessage("Hello, world!");
	        entry1.setCreateDate(new Date());
	        entries.add(entry1);
	        
	        return entries;
	    }

	@Reference
	private TriggerFactory _triggerFactory;
	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;
	
}