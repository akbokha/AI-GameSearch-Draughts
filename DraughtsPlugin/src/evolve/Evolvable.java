/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolve;

import evolve.Properties.AbstractGene;
import java.util.Map;

/**
 *
 * @author s150376
 */
public interface Evolvable {
    
    /**
     * @return The entire genome of the species.
     */
    public Map<String, AbstractGene> getGenome();
    
    /**
     * @param name The name of the gene.
     * @return The gene corresponding with the name of the gene.
     * @throws IllegalArgumentException when the species does have any gene with
     *      the given name.
     */
    public AbstractGene getGene(String name)
            throws IllegalArgumentException;
    
    /**
     * @param name The name of the gene.
     * @param gene The new value of the gene.
     * @throws IllegalArgumentException when the species does have any gene with
     *      the given name.
     */
    public void setGene(String name, AbstractGene gene)
            throws IllegalArgumentException;
    
}
