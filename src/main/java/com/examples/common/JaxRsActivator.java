/**
 *
 * Copyright (c) 2015, Dell GmbH
 * All rights reserved.
 * Copyright (c) 2015, Telekom AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Telekom AG
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Telekom AG.
 * 
 * @since Jan 21, 2015
 * Created by Andrej_Petras
 * 
 */
package com.examples.common;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 6 "no XML" approach to activating
 * JAX-RS.
 * 
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath} annotation.
 * </p>
 * 
 * @author Andrej_Petras
 */
@ApplicationPath("/")
public class JaxRsActivator extends Application {
    /* class body intentionally left blank */
}
