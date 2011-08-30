/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "org.eclipse.rwt" );

org.eclipse.rwt.Display = function( url, rootId ) {
  var req = org.eclipse.swt.Request.getInstance();
  req.setUrl( url );
  req.setUIRootId( rootId );
  qx.core.Init.getInstance().setApplication( new org.eclipse.swt.Application() );
};

org.eclipse.rwt.Display.prototype = {

  allowEvent : function() {
    // NOTE : in the future might need a parameter if there are multiple types of cancelable events 
    org.eclipse.rwt.KeyEventUtil.getInstance().allowEvent();
  },

  cancelEvent : function() {
    org.eclipse.rwt.KeyEventUtil.getInstance().cancelEvent();
  }

};