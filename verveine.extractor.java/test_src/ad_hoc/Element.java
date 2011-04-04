//  Copyright (c) 2007-2008 Adrian Kuhn <akuhn(a)iam.unibe.ch>
//  
//  This file is part of 'Fame (for Java)'.
//  
//  'Fame (for Java)' is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or (at your
//  option) any later version.
//  
//  'Fame (for Java)' is distributed in the hope that it will be useful, but
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
//  License for more details.
//  
//  You should have received a copy of the GNU Lesser General Public License
//  along with 'Fame (for Java)'. If not, see <http://www.gnu.org/licenses/>.
//  

package ad_hoc;

import ad_hoc.FameProperty;
/**
 * Abstract superclass of MSE metamodel.
 */
public abstract class Element {

    private String name;

    public Element() {
    }

    public Element(String name) {
        this.name = name;
    }

    @FameProperty(derived = true)
    public String getFullname() {
        Element parent = this.getOwner();
        return parent == null ? this.getName() : parent.getFullname() + "." + this.getName();
    }

    @FameProperty
    public String getName() {
        return name;
    }

    @FameProperty(derived = true)
    public abstract Element getOwner();

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getFullname();
    }

}
