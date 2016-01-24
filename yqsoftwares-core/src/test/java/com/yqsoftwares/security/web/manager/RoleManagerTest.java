package com.yqsoftwares.security.web.manager;

import com.yqsoftwares.security.Application;
import com.yqsoftwares.security.core.Group;
import com.yqsoftwares.security.core.Role;
import com.yqsoftwares.security.core.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2016-01-17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@Sql(scripts = "init.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(scripts = "destroy.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public class RoleManagerTest {
    @Autowired
    private RoleManager roleManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testAddRole() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_ROLE", "PATH='/USERS/AGENTS'");
        assertTrue(count == 0);

        Role entity = new Role();
        entity.setPath("/USERS/AGENTS");
        entity.setAlias("AGENTS");
        entity.setDescription("Agent Role");

        roleManager.addRole(entity);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_ROLE", "PATH='/USERS/AGENTS'");
        assertTrue(count == 1);
    }

    @Test
    public void testAddUsers() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=1 and ROLE_ID=1");
        assertTrue(count == 1);

        String[] inUsers = new String[]{"user"}; // USER_ID = 2
        roleManager.addUsers("/ADMINS", inUsers);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=1 and ROLE_ID=1");
        assertTrue(count == 1);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=2 and ROLE_ID=1");
        assertTrue(count == 1);
    }

    @Test
    public void testAddGroups() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=2 and ROLE_ID=1");
        assertTrue(count == 0);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=1 and ROLE_ID=1");
        assertTrue(count == 1);

        String[] inGroups = new String[]{"/USERS"}; // GROUP_ID = 2
        roleManager.addGroups("/ADMINS", inGroups);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=2 and ROLE_ID=1");
        assertTrue(count == 1);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=1 and ROLE_ID=1");
        assertTrue(count == 1);
    }

    @Test
    public void testUpdateRole() throws Exception {
        Role role = roleManager.findRole("/ADMINS");
        assertEquals(role.getAlias(), "ADMINS");
        assertEquals(role.getDescription(), "ADMINS");

        role.setAlias("ADMINS_MODIFIED");
        role.setDescription("ADMINS_D_MODIFIED");
        roleManager.updateRole(role);

        role = roleManager.findRole("/ADMINS");
        assertEquals(role.getAlias(), "ADMINS_MODIFIED");
        assertEquals(role.getDescription(), "ADMINS_D_MODIFIED");
    }

    @Test
    public void testUpdateUsers() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=1 and ROLE_ID=1");
        assertTrue(count == 1);

        String[] inUsers = new String[]{"user"}; // USER_ID = 2
        roleManager.updateUsers("/ADMINS", inUsers);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=1 and ROLE_ID=1");
        assertTrue(count == 0);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=2 and ROLE_ID=1");
        assertTrue(count == 1);
    }

    @Test
    public void testUpdateGroups() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=2 and ROLE_ID=1");
        assertTrue(count == 0);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=1 and ROLE_ID=1");
        assertTrue(count == 1);

        String[] inGroups = new String[]{"/USERS"}; // GROUP_ID = 2
        roleManager.updateGroups("/ADMINS", inGroups);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=2 and ROLE_ID=1");
        assertTrue(count == 1);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=1 and ROLE_ID=1");
        assertTrue(count == 0);
    }

    @Test
    public void testRemoveRole() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_ROLE", "PATH='/USERS'");
        assertTrue(count == 1);

        roleManager.removeRole("/USERS");

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_ROLE", "PATH='/USERS'");
        assertTrue(count == 0);
    }

    @Test
    public void testRemoveUsers() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=2 AND ROLE_ID=2");
        assertTrue(count == 1);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=1 AND ROLE_ID=2");
        assertTrue(count == 1);

        roleManager.removeUsers("/USERS", "user");

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=2 AND ROLE_ID=2");
        assertTrue(count == 0);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=1 AND ROLE_ID=2");
        assertTrue(count == 1);
    }

    @Test
    public void testRemoveGroups() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=1 AND ROLE_ID=1");
        assertTrue(count == 1);

        roleManager.removeGroups("/ADMINS", "/ADMINS");

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=1 AND ROLE_ID=1");
        assertTrue(count == 0);
    }

    @Test
    public void testAddRoleWithUsersAndGroups() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_ROLE", "PATH='/USERS/AGENTS'");
        assertTrue(count == 0);

        Role entity = new Role();
        entity.setPath("/USERS/AGENTS");
        entity.setAlias("AGENTS");
        entity.setDescription("Agent Group");

        String[] inUsers = new String[]{"user"};
        String[] inGroups = new String[]{"/USERS"};

        roleManager.addRole(entity, Arrays.asList(inUsers), Arrays.asList(inGroups));

        Role createdRole = roleManager.findRole("/USERS/AGENTS");
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "ROLE_ID=" + createdRole.getId());
        assertTrue(count == 1);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "ROLE_ID=" + createdRole.getId());
        assertTrue(count == 1);
    }

    @Test
    public void testUpdateRoleWithUsersAndGroups() throws Exception {
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=2 and ROLE_ID=1");
        assertTrue(count == 0);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=1 and ROLE_ID=1");
        assertTrue(count == 1);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=1 AND ROLE_ID=1");
        assertTrue(count == 1);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=2 AND ROLE_ID=1");
        assertTrue(count == 0);

        Role entity = roleManager.findRole("/ADMINS");

        String[] inUsers = new String[]{"user"};
        String[] inGroups = new String[]{"/USERS"};

        roleManager.updateRole(entity, Arrays.asList(inUsers), Arrays.asList(inGroups));

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=2 and ROLE_ID=1");
        assertTrue(count == 1);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_USER_ROLES", "USER_ID=1 and ROLE_ID=1");
        assertTrue(count == 0);

        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=1 AND ROLE_ID=1");
        assertTrue(count == 0);
        count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "SEC_GROUP_ROLES", "GROUP_ID=2 AND ROLE_ID=1");
        assertTrue(count == 1);
    }

    @Test
    public void testHasRole() throws Exception {
        boolean result = roleManager.hasRole("/USERS");
        assertTrue(result);

        result = roleManager.hasRole("nonexistedrole");
        assertFalse(result);
    }

    @Test
    public void testFindRole() throws Exception {
        Role role = roleManager.findRole("/USERS");
        assertNotNull(role);
    }

    @Test
    public void testFindRoles() throws Exception {
        Page<Role> roles = roleManager.findRoles("nonexistedrole", new PageRequest(0, 10));
        assertFalse(roles.hasContent());
    }

    @Test
    public void testFindAllUsers() throws Exception {
        Page<User> users = roleManager.findAllUsers(new PageRequest(0, 10));
        assertTrue(users.getContent().size() == 2);
    }

    @Test
    public void testFindAllGroups() throws Exception {
        Page<Group> groups = roleManager.findAllGroups(new PageRequest(0, 10));
        assertTrue(groups.getContent().size() == 2);
    }
}