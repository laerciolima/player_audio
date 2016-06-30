/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Laercio Lima
 */
public class PlayList implements Serializable {

    private String nome = "";
    private ArrayList<File> musicas = new ArrayList<File>();

    public PlayList(ArrayList<File> f) {
        musicas = f;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<File> getMusicas() {
        return musicas;
    }

    public void setMusicas(ArrayList<File> musicas) {
        this.musicas = musicas;
    }

    public boolean salvar(String dir) {
        try {
            FileOutputStream fo = new FileOutputStream(dir);
            ObjectOutputStream oo = new ObjectOutputStream(fo);
            oo.writeObject(this);
            oo.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean abrir(String dir) {
        try {
            FileInputStream fi = new FileInputStream(dir);
            ObjectInputStream oi = new ObjectInputStream(fi);
            PlayList pl = (PlayList) oi.readObject();
            for (File f : pl.getMusicas()) {
                musicas.add(f);
            }

            oi.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
