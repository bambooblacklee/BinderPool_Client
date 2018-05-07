// ISecurityCenter.aidl
package com.bamboolmc.binderpool;

interface ISecurityCenter {
    String encrypt(String content);
    String decrypt(String password);
}
