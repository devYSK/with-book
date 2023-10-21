/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ys.itemreader.domain;

// import javax.persistence.Column;
// import javax.persistence.Entity;
// import javax.persistence.Id;
// import javax.persistence.Table;

import javax.persistence.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table(name = "CUSTOMER")
public class Customer {

	@Id
	private Long id;
	private String firstName;
	private String middleInitial;
	private String lastName;
	private String addressNumber;
	private String street;
	private String address;
	private String city;
	private String state;
	private String zipCode;

	public Customer() {
	}

	public Customer(String firstName, String middleName, String lastName, String addressNumber, String street, String city, String state, String zipCode) {
		this.firstName = firstName;
		this.middleInitial = middleName;
		this.lastName = lastName;
		this.addressNumber = addressNumber;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
	}

	@Override
	public String toString() {
		return "Customer{" +
			"firstName='" + firstName + '\'' +
			", middleInitial='" + middleInitial + '\'' +
			", lastName='" + lastName + '\'' +
			", address='" + address + '\'' +
			", addressNumber='" + addressNumber + '\'' +
			", street='" + street + '\'' +
			", city='" + city + '\'' +
			", state='" + state + '\'' +
			", zipCode='" + zipCode + '\'' +
			'}';
	}
}