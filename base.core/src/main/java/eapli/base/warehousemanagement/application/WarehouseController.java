package eapli.base.warehousemanagement.application;

import eapli.base.infrastructure.persistence.PersistenceContext;
import eapli.base.warehousemanagement.JsonParser;
import eapli.base.warehousemanagement.domain.Aisle;
import eapli.base.warehousemanagement.domain.Row;
import eapli.base.warehousemanagement.domain.Warehouse;
import eapli.base.warehousemanagement.repositories.WarehouseRepository;

import java.io.FileNotFoundException;
import java.util.Iterator;

public class WarehouseController {
    private final WarehouseRepository repo = PersistenceContext.repositories().warehouse();

    public Warehouse buildWarehousePlant(String fileName) throws FileNotFoundException {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.readJson(fileName);
    }

    public void buildShelves(Warehouse warehouse) throws FileNotFoundException {
        for (Aisle aile : warehouse.aisles()) {
            for (Row row : aile.rows()) {
                row.convertShelveNumberToPosition();
            }
        }
    }

    public boolean alreadyInDatabase() {
        int contagem = 0;
        while (repo.findAllActive().iterator().hasNext()) {
            contagem++;
        }
        return contagem > 0;
    }

    public void deletePreviousWarehouse() {
        for (Warehouse warehouse : repo.findAllActive()) {
            repo.remove(warehouse);
        }
    }

    public void saveWarehouse(Warehouse warehouse) {
        repo.save(warehouse);
    }
}
