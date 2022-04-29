package eapli.base.persistence.impl.jpa;

import eapli.base.Application;
import eapli.base.agv.domain.AGV;
import eapli.base.agv.repositories.AGVRepository;
import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JpaAGVRepository extends JpaAutoTxRepository<AGV,Long, Long> implements AGVRepository {

    public JpaAGVRepository(final String puname) {
        super(puname, Application.settings().getExtendedPersistenceProperties(), "id");
    }

    public JpaAGVRepository(final TransactionalContext autoTx) {
        super(autoTx, "id");
    }

    @Override
    public Iterable<AGV> findAllActive() {
        return match("e.systemUser.active = true");
    }

    @Override
    public Optional<AGV> findById(final Long number) {
        final Map<String, Object> params = new HashMap<>();
        params.put("number", number);
        return matchOne("e.id=:number", params);
    }
}