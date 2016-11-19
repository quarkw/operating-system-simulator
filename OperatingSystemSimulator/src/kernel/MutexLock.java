/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kernel;

public class MutexLock {
    
    private boolean inUse = false;
    
    public boolean aquire() {
        if (inUse) {
            return false;
        } else {
            inUse = true;
            return true;
        }
    }
    
    public void release() {
        inUse = false;
    }
    
}
