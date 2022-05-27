/*
 * Copyright (c) 2013-2021 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eapli.base.infrastructure.bootstrapers;

import java.util.HashSet;
import java.util.Set;

import eapli.base.usermanagement.domain.BaseRoles;
import eapli.framework.actions.Action;
import eapli.framework.infrastructure.authz.domain.model.Role;

/**
 * @author Paulo Gandra Sousa
 */
public class MasterUsersBootstrapper extends UsersBootstrapperBase implements Action {

    @Override
    public boolean execute() {
        registerAdmin("admin", TestDataConstants.PASSWORD1, "Jane", "Doe Admin",
                "jane.doe@email.local");
        Set <Role> SALESCLERK = new HashSet<>();
        SALESCLERK.add(BaseRoles.SALES_CLERK);
        registerUser("user1","Password1","Joe","Jacare","joejones@mail.com",SALESCLERK);

        Set <Role> SALESMANAGER = new HashSet<>();
        SALESMANAGER.add(BaseRoles.SALES_MANAGER);
        registerUser("user2","Password2","Paul","Pera","paulpera@mail.com",SALESMANAGER);

        Set <Role> WAREHOUSEEMPLYEE = new HashSet<>();
        WAREHOUSEEMPLYEE.add(BaseRoles.WAREHOUSE_EMPLOYEE);
        registerUser("user3","Password3","Luigi","Lemon","joejones@mail.com",WAREHOUSEEMPLYEE);

        /*Set <Role> PROJECTMANAGER = new HashSet<>();
        PROJECTMANAGER.add(BaseRoles.PROJECT_MANAGER);
        registerUser("user4","Password4","Kyle","Monteiro","kylemonteiro@mail.com",PROJECTMANAGER);*/

        return true;
    }

    /**
     *
     */
    private void registerAdmin(final String username, final String password, final String firstName,
            final String lastName, final String email) {
        final Set<Role> roles = new HashSet<>();
        roles.add(BaseRoles.ADMIN);

        registerUser(username, password, firstName, lastName, email, roles);
    }
}
