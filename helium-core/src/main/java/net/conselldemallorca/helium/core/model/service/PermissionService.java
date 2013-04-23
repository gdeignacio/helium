/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.model.hibernate.GenericEntity;
import net.conselldemallorca.helium.core.security.acl.AclServiceDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.MutableAcl;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.security.acls.sid.GrantedAuthoritySid;
import org.springframework.security.acls.sid.PrincipalSid;
import org.springframework.security.acls.sid.Sid;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar permisos per als objectes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PermissionService {

	private AclServiceDao aclServiceDao;



	@SuppressWarnings("rawtypes")
	public void addPermissions(
			String recipient,
			boolean principal,
			Set<Permission> permissions,
			Serializable id,
			Class clazz,
			boolean granting) {
		Sid sid = (principal) ? new PrincipalSid(recipient) : new GrantedAuthoritySid(recipient);
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(clazz, id);
		MutableAcl acl = null;
		try {
			acl = aclServiceDao.readMutableAclById(objectIdentity);
		} catch (NotFoundException nfe) {
			acl = aclServiceDao.createAcl(objectIdentity);
		}
		for (Permission perm: permissions) {
			boolean insertar;
			try {
				insertar = !acl.isGranted(new Permission[] {perm}, new Sid[] {sid}, false);
			} catch (Exception ignored) {
				insertar = true;
			}
			if (insertar)
				acl.insertAce(
						acl.getEntries().length,
						perm,
						sid,
						true);
		}
		aclServiceDao.updateAcl(acl);
	}
	@SuppressWarnings("rawtypes")
	public void deletePermissions(
			String recipient,
			boolean principal,
			Set<Permission> permissions,
			Serializable id,
			Class clazz,
			boolean granting) {
		if (principal) {
			for (Permission permission: permissions)
				aclServiceDao.deleteAclEntry(
						new PrincipalSid(recipient),
						id,
						clazz,
						permission,
						granting);
		} else {
			for (Permission permission: permissions)
				aclServiceDao.deleteAclEntry(
						new GrantedAuthoritySid(recipient),
						id,
						clazz,
						permission,
						granting);
		}
	}
	@SuppressWarnings("rawtypes")
	public void deleteAllPermissionsForSid(
			String recipient,
			boolean principal,
			Serializable id,
			Class clazz) {
		if (principal) {
			aclServiceDao.deleteAclEntriesForSid(
					new PrincipalSid(recipient),
					id,
					clazz);
		} else {
			aclServiceDao.deleteAclEntriesForSid(
					new GrantedAuthoritySid(recipient),
					id,
					clazz);
		}
	}
	@SuppressWarnings("rawtypes")
	public Map<Sid, List<AccessControlEntry>> getAclEntriesGroupedBySid(
			Serializable id,
			Class clazz) {
		ObjectIdentity oid = new ObjectIdentityImpl(clazz, id);
		try {
			Acl acl = aclServiceDao.readAclById(oid);
			Map<Sid, List<AccessControlEntry>> resposta = new HashMap<Sid, List<AccessControlEntry>>();
			for (AccessControlEntry ace: acl.getEntries()) {
				List<AccessControlEntry> entriesForSid = resposta.get(ace.getSid());
				if (entriesForSid == null) {
					entriesForSid = new ArrayList<AccessControlEntry>();
					resposta.put(ace.getSid(), entriesForSid);
				}
				entriesForSid.add(ace);
			}
			return resposta;
		} catch (NotFoundException ex) {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public void filterAllowed(
			List list,
			Class clazz,
			Permission[] permissions) {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object entry = it.next();
			if (!isGrantedAny((GenericEntity)entry, clazz, permissions))
				it.remove();
		}
	}
	@SuppressWarnings("rawtypes")
	public Object filterAllowed(
			GenericEntity object,
			Class clazz,
			Permission[] permissions) {
		if (isGrantedAny(object, clazz, permissions)) {
			return object;
		} else {
			return null;
		}
	}



	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}

	@SuppressWarnings("rawtypes")
	private boolean isGrantedAny(
			GenericEntity object,
			Class clazz,
			Permission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities()) {
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		}
		try {
			Acl acl = aclServiceDao.readAclById(new ObjectIdentityImpl(clazz, object.getId()));
			for (Permission perm : permissions) {
				try {
					if (acl.isGranted(
							new Permission[]{perm},
							sids.toArray(new Sid[sids.size()]),
							false)) {
						return true;
					}
				} catch (NotFoundException ex) {}
			}
			return false;
		} catch (NotFoundException ex) {
			return false;
		}
	}
}
