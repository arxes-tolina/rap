/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getAdapter;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import java.lang.reflect.Field;

import org.eclipse.rap.rwt.internal.util.ActiveKeysUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ControlUtil;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.internal.widgets.IControlHolderAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;


public class ControlLCAUtil {

  // Property names to preserve widget property values
  private static final String PROP_ACTIVATE_LISTENER = "Activate";
  private static final String PROP_DEACTIVATE_LISTENER = "Deactivate";
  private static final String PROP_FOCUS_IN_LISTENER = "FocusIn";
  private static final String PROP_FOCUS_OUT_LISTENER = "FocusOut";
  private static final String PROP_MOUSE_DOWN_LISTENER = "MouseDown";
  private static final String PROP_MOUSE_DOUBLE_CLICK_LISTENER = "MouseDoubleClick";
  private static final String PROP_MOUSE_UP_LISTENER = "MouseUp";
  private static final String PROP_KEY_LISTENER = "KeyDown";
  private static final String PROP_TRAVERSE_LISTENER = "Traverse";
  private static final String PROP_MENU_DETECT_LISTENER = "MenuDetect";
  private static final String PROP_TAB_INDEX = "tabIndex";
  private static final String PROP_CURSOR = "cursor";
  private static final String PROP_BACKGROUND_IMAGE = "backgroundImage";
  private static final String PROP_CHILDREN = "children";
  private static final String PROP_PARENT = "parent";

  private static final String CURSOR_UPARROW
    = "rwt-resources/resource/widget/rap/cursors/up_arrow.cur";

  private ControlLCAUtil() {
    // prevent instance creation
  }

  public static void preserveValues( Control control ) {
    preserveParent( control );
    preserveChildren( control );
    preserveBounds( control );
    preserveTabIndex( control );
    preserveToolTipText( control );
    preserveMenu( control );
    preserveVisible( control );
    preserveEnabled( control );
    preserveForeground( control );
    preserveBackground( control );
    preserveBackgroundImage( control );
    preserveFont( control );
    preserveCursor( control );
    preserveData( control );
    ActiveKeysUtil.preserveActiveKeys( control );
    ActiveKeysUtil.preserveCancelKeys( control );
    preserveListenActivate( control );
    preserveListenMouse( control );
    preserveListenFocus( control );
    preserveListenKey( control );
    preserveListenTraverse( control );
    preserveListenMenuDetect( control );
    preserveListenHelp( control );
  }

  public static void renderChanges( Control control ) {
    renderParent( control );
    renderChildren( control );
    renderBounds( control );
    renderTabIndex( control );
    renderToolTipText( control );
    renderMenu( control );
    renderVisible( control );
    renderEnabled( control );
    renderForeground( control );
    renderBackground( control );
    renderBackgroundImage( control );
    renderFont( control );
    renderCursor( control );
    renderData( control );
    ActiveKeysUtil.renderActiveKeys( control );
    ActiveKeysUtil.renderCancelKeys( control );
    renderListenActivate( control );
    renderListenMouse( control );
    renderListenFocus( control );
    renderListenKey( control );
    renderListenTraverse( control );
    renderListenMenuDetect( control );
    renderListenHelp( control );
  }

  private static void preserveParent( Control control ) {
    Composite parent = control.getParent();
    if( parent != null ) {
      WidgetLCAUtil.preserveProperty( control, PROP_PARENT, getId( control.getParent() ) );

    }
  }

  private static void renderParent( Control control ) {
    RemoteAdapter adapter = getAdapter( control );
    Composite parent = control.getParent();
    if( adapter.isInitialized() && parent != null ) {
      WidgetLCAUtil.renderProperty( control, PROP_PARENT, getId( parent ), null );
    }
  }

  private static void preserveChildren( Control control ) {
    getAdapter( control ).preserve( PROP_CHILDREN, getChildren( control ) );
  }

  private static void renderChildren( Control control ) {
    if( control instanceof Composite ) {
      String[] newValue = getChildren( control );
      WidgetLCAUtil.renderProperty( control, PROP_CHILDREN, newValue, null );
    }
  }

  private static void preserveBounds( Control control ) {
    WidgetLCAUtil.preserveBounds( control, control.getBounds() );
  }

  private static void renderBounds( Control control ) {
    WidgetLCAUtil.renderBounds( control, control.getBounds() );
  }

  private static void preserveTabIndex( Control control ) {
    getAdapter( control ).preserve( PROP_TAB_INDEX, Integer.valueOf( getTabIndex( control ) ) );
  }

  private static void renderTabIndex( Control control ) {
    if( control instanceof Shell ) {
      resetTabIndices( ( Shell )control );
      // tabIndex must be a positive value
      computeTabIndices( ( Shell )control, 1 );
    }
    int tabIndex = getTabIndex( control );
    Integer newValue = Integer.valueOf( tabIndex );
    // there is no reliable default value for all controls
    if( WidgetLCAUtil.hasChanged( control, PROP_TAB_INDEX, newValue ) ) {
      getRemoteObject( control ).set( "tabIndex", tabIndex );
    }
  }

  private static void preserveToolTipText( Control control ) {
    WidgetLCAUtil.preserveToolTipText( control, control.getToolTipText() );
  }

  private static void renderToolTipText( Control control ) {
    WidgetLCAUtil.renderToolTip( control, control.getToolTipText() );
  }

  private static void preserveMenu( Control control ) {
    getAdapter( control ).preserve( Props.MENU, control.getMenu() );
  }

  private static void renderMenu( Control control ) {
    WidgetLCAUtil.renderMenu( control, control.getMenu() );
  }

  private static void preserveVisible( Control control ) {
    getAdapter( control ).preserve( Props.VISIBLE, Boolean.valueOf( getVisible( control ) ) );
  }

  private static void renderVisible( Control control ) {
    boolean visible = getVisible( control );
    Boolean newValue = Boolean.valueOf( visible );
    Boolean defValue = control instanceof Shell ? Boolean.FALSE : Boolean.TRUE;
    // TODO [tb] : Can we have a shorthand for this, like in JSWriter?
    if( WidgetLCAUtil.hasChanged( control, Props.VISIBLE, newValue, defValue ) ) {
      getRemoteObject( control ).set( "visibility", visible );
    }
  }

  private static void preserveEnabled( Control control ) {
    WidgetLCAUtil.preserveEnabled( control, control.getEnabled() );
  }

  private static void renderEnabled( Control control ) {
    // Using isEnabled() would result in unnecessarily updating child widgets of
    // enabled/disabled controls.
    WidgetLCAUtil.renderEnabled( control, control.getEnabled() );
  }

  private static void preserveForeground( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    WidgetLCAUtil.preserveForeground( control, controlAdapter.getUserForeground() );
  }

  private static void renderForeground( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    WidgetLCAUtil.renderForeground( control, controlAdapter.getUserForeground() );
  }

  private static void preserveBackground( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    WidgetLCAUtil.preserveBackground( control,
                                      controlAdapter.getUserBackground(),
                                      controlAdapter.getBackgroundTransparency() );
  }

  private static void renderBackground( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    WidgetLCAUtil.renderBackground( control,
                                    controlAdapter.getUserBackground(),
                                    controlAdapter.getBackgroundTransparency() );
  }

  private static void preserveBackgroundImage( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    Image image = controlAdapter.getUserBackgroundImage();
    getAdapter( control ).preserve( PROP_BACKGROUND_IMAGE, image );
  }

  private static void renderBackgroundImage( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    Image image = controlAdapter.getUserBackgroundImage();
    WidgetLCAUtil.renderProperty( control, PROP_BACKGROUND_IMAGE, image, null );
  }

  private static void preserveFont( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    WidgetLCAUtil.preserveFont( control, controlAdapter.getUserFont() );
  }

  private static void renderFont( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    WidgetLCAUtil.renderFont( control, controlAdapter.getUserFont() );
  }

  private static void preserveCursor( Control control ) {
    getAdapter( control ).preserve( PROP_CURSOR, control.getCursor() );
  }

  private static void renderCursor( Control control ) {
    Cursor newValue = control.getCursor();
    if( WidgetLCAUtil.hasChanged( control, PROP_CURSOR, newValue, null ) ) {
      getRemoteObject( control ).set( PROP_CURSOR, getQxCursor( newValue ) );
    }
  }

  private static void preserveData( Control control ) {
    WidgetLCAUtil.preserveData( control );
  }

  private static void renderData( Control control ) {
    WidgetLCAUtil.renderData( control );
  }

  private static void preserveListenActivate( Control control ) {
    // Note: Shell "Activate" event is handled by ShellLCA
    if( !( control instanceof Shell ) ) {
      WidgetLCAUtil.preserveListener( control,
                                      PROP_ACTIVATE_LISTENER,
                                      isListening( control, SWT.Activate ) );
      WidgetLCAUtil.preserveListener( control,
                                      PROP_DEACTIVATE_LISTENER,
                                      isListening( control, SWT.Deactivate ) );
    }
  }

  private static void renderListenActivate( Control control ) {
    // Note: Shell "Activate" event is handled by ShellLCA
    if( !( control instanceof Shell ) ) {
      renderListen( control, SWT.Activate, PROP_ACTIVATE_LISTENER );
      renderListen( control, SWT.Deactivate, PROP_DEACTIVATE_LISTENER );
    }
  }

  private static void preserveListenMouse( Control control ) {
    WidgetLCAUtil.preserveListener( control,
                                    PROP_MOUSE_DOWN_LISTENER,
                                    isListening( control, SWT.MouseDown ) );
    WidgetLCAUtil.preserveListener( control,
                                    PROP_MOUSE_UP_LISTENER,
                                    isListening( control, SWT.MouseUp ) );
    WidgetLCAUtil.preserveListener( control,
                                    PROP_MOUSE_DOUBLE_CLICK_LISTENER,
                                    isListening( control, SWT.MouseDoubleClick ) );
  }

  private static void renderListenMouse( Control control ) {
    renderListen( control, SWT.MouseDown, PROP_MOUSE_DOWN_LISTENER );
    renderListen( control, SWT.MouseUp, PROP_MOUSE_UP_LISTENER );
    renderListen( control, SWT.MouseDoubleClick, PROP_MOUSE_DOUBLE_CLICK_LISTENER );
  }

  private static void preserveListenFocus( Control control ) {
    if( ( control.getStyle() & SWT.NO_FOCUS ) == 0 ) {
      WidgetLCAUtil.preserveListener( control,
                                      PROP_FOCUS_IN_LISTENER,
                                      isListening( control, SWT.FocusIn ) );
      WidgetLCAUtil.preserveListener( control,
                                      PROP_FOCUS_OUT_LISTENER,
                                      isListening( control, SWT.FocusOut ) );
    }
  }

  private static void renderListenFocus( Control control ) {
    if( ( control.getStyle() & SWT.NO_FOCUS ) == 0 ) {
      renderListen( control, SWT.FocusIn, PROP_FOCUS_IN_LISTENER );
      renderListen( control, SWT.FocusOut, PROP_FOCUS_OUT_LISTENER );
    }
  }

  private static void preserveListenKey( Control control ) {
    WidgetLCAUtil.preserveListener( control,
                                    PROP_KEY_LISTENER,
                                    hasKeyListener( control ) );
  }

  private static void renderListenKey( Control control ) {
    boolean newValue = hasKeyListener( control );
    WidgetLCAUtil.renderListener( control, PROP_KEY_LISTENER, newValue, false );
  }

  private static void preserveListenTraverse( Control control ) {
    WidgetLCAUtil.preserveListener( control,
                                    PROP_TRAVERSE_LISTENER,
                                    isListening( control, SWT.Traverse ) );
  }

  private static void renderListenTraverse( Control control ) {
    boolean newValue = isListening( control, SWT.Traverse );
    WidgetLCAUtil.renderListener( control, PROP_TRAVERSE_LISTENER, newValue, false );
  }

  private static void preserveListenMenuDetect( Control control ) {
    WidgetLCAUtil.preserveListener( control,
                                    PROP_MENU_DETECT_LISTENER,
                                    isListening( control, SWT.MenuDetect ) );
  }

  private static void renderListenMenuDetect( Control control ) {
    boolean newValue = isListening( control, SWT.MenuDetect );
    WidgetLCAUtil.renderListener( control, PROP_MENU_DETECT_LISTENER, newValue, false );
  }

  private static void preserveListenHelp( Control control ) {
    WidgetLCAUtil.preserveHelpListener( control );
  }

  private static void renderListenHelp( Control control ) {
    WidgetLCAUtil.renderListenHelp( control );
  }

  private static void renderListen( Control control, int eventType, String eventName ) {
    WidgetLCAUtil.renderListener( control, eventName, isListening( control, eventType ), false );
  }

  private static String[] getChildren( Control control ) {
    String[] result = null;
    if( control instanceof Composite ) {
      Composite composite = ( Composite )control;
      IControlHolderAdapter controlHolder = composite.getAdapter( IControlHolderAdapter.class );
      Control[] children = controlHolder.getControls();
      result = new String[ children.length ];
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = getId( children[ i ] );
      }
    }
    return result;
  }

  // [if] Fix for bug 263025, 297466, 223873 and more
  // some qooxdoo widgets with size (0,0) are not invisible
  private static boolean getVisible( Control control ) {
    Point size = control.getSize();
    return control.getVisible() && size.x > 0 && size.y > 0;
  }

  // TODO [rh] Eliminate instance checks. Let the respective classes always return NO_FOCUS
  private static boolean takesFocus( Control control ) {
    boolean result = true;
    result &= ( control.getStyle() & SWT.NO_FOCUS ) == 0;
    result &= control.getClass() != Composite.class;
    result &= control.getClass() != SashForm.class;
    return result;
  }

  private static int getTabIndex( Control control ) {
    int result = -1;
    if( takesFocus( control ) ) {
      result = ControlUtil.getControlAdapter( control ).getTabIndex();
    }
    return result;
  }

  private static void resetTabIndices( Composite composite ) {
    for( Control control : composite.getChildren() ) {
      ControlUtil.getControlAdapter( control ).setTabIndex( -1 );
      if( control instanceof Composite ) {
        resetTabIndices( ( Composite )control );
      }
    }
  }

  private static int computeTabIndices( Composite composite, int startIndex ) {
    int result = startIndex;
    for( Control control : composite.getTabList() ) {
      IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
      controlAdapter.setTabIndex( result );
      // for Links, leave a range out to be assigned to hrefs on the client
      result += control instanceof Link ? 300 : 1;
      if( control instanceof Composite ) {
        result = computeTabIndices( ( Composite )control, result );
      }
    }
    return result;
  }

  private static String getQxCursor( Cursor newValue ) {
    String result = null;
    if( newValue != null ) {
      // TODO [rst] Find a better way of obtaining the Cursor value
      // TODO [tb] adjust strings to match name of constants
      int value = 0;
      try {
        Class cursorClass = Cursor.class;
        Field field = cursorClass.getDeclaredField( "value" );
        field.setAccessible( true );
        value = field.getInt( newValue );
      } catch( Exception e ) {
        throw new RuntimeException( e );
      }
      switch( value ) {
        case SWT.CURSOR_ARROW:
          result = "default";
        break;
        case SWT.CURSOR_WAIT:
          result = "wait";
        break;
        case SWT.CURSOR_APPSTARTING:
          result = "progress";
          break;
        case SWT.CURSOR_CROSS:
          result = "crosshair";
        break;
        case SWT.CURSOR_HELP:
          result = "help";
        break;
        case SWT.CURSOR_SIZEALL:
          result = "move";
        break;
        case SWT.CURSOR_SIZENS:
          result = "row-resize";
        break;
        case SWT.CURSOR_SIZEWE:
          result = "col-resize";
        break;
        case SWT.CURSOR_SIZEN:
          result = "n-resize";
        break;
        case SWT.CURSOR_SIZES:
          result = "s-resize";
        break;
        case SWT.CURSOR_SIZEE:
          result = "e-resize";
        break;
        case SWT.CURSOR_SIZEW:
          result = "w-resize";
        break;
        case SWT.CURSOR_SIZENE:
        case SWT.CURSOR_SIZENESW:
          result = "ne-resize";
        break;
        case SWT.CURSOR_SIZESE:
          result = "se-resize";
        break;
        case SWT.CURSOR_SIZESW:
          result = "sw-resize";
        break;
        case SWT.CURSOR_SIZENW:
        case SWT.CURSOR_SIZENWSE:
          result = "nw-resize";
        break;
        case SWT.CURSOR_IBEAM:
          result = "text";
        break;
        case SWT.CURSOR_HAND:
          result = "pointer";
        break;
        case SWT.CURSOR_NO:
          result = "not-allowed";
        break;
        case SWT.CURSOR_UPARROW:
          result = CURSOR_UPARROW;
        break;
      }
    }
    return result;
  }

  private static boolean hasKeyListener( Control control ) {
    return isListening( control, SWT.KeyUp ) || isListening( control, SWT.KeyDown );
  }

}
