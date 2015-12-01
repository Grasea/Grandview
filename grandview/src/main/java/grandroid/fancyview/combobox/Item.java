/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview.combobox;

import grandroid.database.Identifiable;

/**
 *
 * @author Rovers
 */
public class Item implements Identifiable {

    protected Integer id = 0;
    protected String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer get_id() {
        return id;
    }

    public void set_id(Integer intgr) {
        this.id = intgr;
    }

    @Override
    public String toString() {
        return name;
    }
}
