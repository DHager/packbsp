/*
 * 
 */
package com.technofovea.packbsp.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interprets and stores data about which profiles are available to the application.
 * Each profile defines an arrangement of Spring configuration files which can be
 * used to customize the behavior of the application for different situations.
 * 
 * @author Darien Hager
 */
@XmlRootElement(name = "profiles")
public class ProfileList implements List<Profile> {

    public static List<Profile> load(File source) throws JAXBException{        
        JAXBContext jc = JAXBContext.newInstance(ProfileList.class, Profile.class);
        Unmarshaller um = jc.createUnmarshaller();
        return new ArrayList<Profile>((ProfileList) um.unmarshal(source));
    }
    @XmlElement(name = "profile")
    protected List<Profile> delegate = new ArrayList<Profile>();

    public ProfileList() {
    }

    public ProfileList(List<Profile> list) {
        this.delegate.addAll(list);
    }

    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public List<Profile> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    public int size() {
        return delegate.size();
    }

    public Profile set(int index, Profile element) {
        return delegate.set(index, element);
    }

    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    public Profile remove(int index) {
        return delegate.remove(index);
    }

    @SuppressWarnings("element-type-mismatch")
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    public ListIterator<Profile> listIterator(int index) {
        return delegate.listIterator(index);
    }

    public ListIterator<Profile> listIterator() {
        return delegate.listIterator();
    }

    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    public Iterator<Profile> iterator() {
        return delegate.iterator();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public Profile get(int index) {
        return delegate.get(index);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @SuppressWarnings("element-type-mismatch")
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public void clear() {
        delegate.clear();
    }

    public boolean addAll(int index, Collection<? extends Profile> c) {
        return delegate.addAll(index, c);
    }

    public boolean addAll(Collection<? extends Profile> c) {
        return delegate.addAll(c);
    }

    public void add(int index, Profile element) {
        delegate.add(index, element);
    }

    public boolean add(Profile e) {
        return delegate.add(e);
    }

    @Override
    public String toString() {
        return "ProfileList{" + "delegate=" + delegate + "}";
    }
    
}
