package org.example.service;

import org.example.dao.LinkDao;
import org.example.domain.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private LinkDao linkDao;

    @Autowired
    public void setLinkDao(LinkDao linkDao) {
        this.linkDao = linkDao;
    }
    public String save(String email) {
        if(linkDao.isLinkExist(email)) {
            String uuid = linkDao.getLinkByEmail(email).getCode();
            linkDao.updateTime(email);
            return uuid;
        }
        String uuid = genUuid();
        linkDao.addLink(email, uuid);
        return uuid;
    }

    public String genUuid() {
        String uuid = java.util.UUID.randomUUID().toString();
        while (linkDao.isUuidExist(uuid)) {
            uuid = java.util.UUID.randomUUID().toString();
        }
        return uuid;
    }

    public String verify(String uuid) {
        if(!linkDao.isUuidExist(uuid)) {
            return null;
        }
        String email = linkDao.getLinkByUuid(uuid).getEmail();
        if(!linkDao.checkIfExpired(email)) {
            return null;
        }
        return email;

    }
}
