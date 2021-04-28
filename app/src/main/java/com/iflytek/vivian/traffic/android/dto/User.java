/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.iflytek.vivian.traffic.android.dto;

public class User {
    private String id;
    private String name;
    private String age;
    private String password;
    private Integer isAdmin;
    private String role;
    private String department;

    public User() {

    }

    /**
     * 用于登录传参
     * @param id
     * @param password
     */
    public User(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public User(String id, String name, String role, String department) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.department = department;
    }

    public User(String id, String name, String imageId, Integer isAdmin, String role, String department) {
        this.id = id;
        this.name = name;
        this.password = imageId;
        this.isAdmin = isAdmin;
        this.role = role;
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }
}
