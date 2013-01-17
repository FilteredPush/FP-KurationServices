/* Copyright © 2011 President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of Version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.filteredpush.specify.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.filteredpush.specify.hibernate.SpecifyHibernateSession;

/**
 * See https://community.jboss.org/wiki/UsingHibernateWithTomcat
 * 
 * Author: mkelly
 *
 * $Id:$
 */
public class HibernateListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		SpecifyHibernateSession.getSessionFactory();		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		SpecifyHibernateSession.getSessionFactory().close();		
	}
}
