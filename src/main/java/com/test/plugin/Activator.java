package com.test.plugin;

import java.util.ArrayList;
import java.util.Collection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;

    public void start(BundleContext context) {
        registrationList = new ArrayList<ServiceRegistration>();

        //Register plugin here
        //registrationList.add(context.registerService(MyPlugin.class.getName(), new MyPlugin(), null));
        registrationList.add(context.registerService(MappingApproval.class.getName(), new MappingApproval(), null));
        registrationList.add(context.registerService(UpdateApproval.class.getName(), new UpdateApproval(), null));
//        registrationList.add(context.registerService(MultiApproval.class.getName(), new MultiApproval(), null));
//        registrationList.add(context.registerService(TestDataListAction.class.getName(), new TestDataListAction(), null));
    }

    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}