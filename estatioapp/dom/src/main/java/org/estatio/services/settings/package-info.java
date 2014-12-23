/**
 * Defines services for maintaining application- and user-level settings.
 * 
 * <p>
 * These are Estatio-specific versions of {@link org.isisaddons.module.settings.dom.UserSettingsService service}
 * {@link org.isisaddons.module.settings.dom.ApplicationSettingsService implementations} provided by Isis itself.
 * 
 * <p>
 * The only setting currently held by Estatio is the &quot;epochDate&quot;, indicating the cutover date of data 
 * migrated into the system.
 */
package org.estatio.services.settings;