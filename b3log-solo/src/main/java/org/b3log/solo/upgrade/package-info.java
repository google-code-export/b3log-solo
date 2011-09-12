/**
 * Upgraders.
 * 
 * <p>
 * Since B3log Solo 0.3.1, B3log Solo will check automatically for upgrading.
 * See the <a href="http://code.google.com/p/b3log-solo/issues/detail?id=257">issue 257</a>
 * for more details.
 * </p>
 * 
 * @deprecated As of Solo 0.3.1, upgrade will check automatically, so removes 
 * all upgrader URL mappings in web.xml, with no replacement. See 
 * {@link org.b3log.solo.web.processor.UpgradeProcessor} for mor details.
 */
package org.b3log.solo.upgrade;
