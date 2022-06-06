export interface NewContact {
  contactId: string;
  firstname: string;
  lastname: string;
  birthday: string;
  email: string;
  phoneNumber: string;
  addresses: Address[];
}
export interface Contact extends NewContact {
  id: string;
  avatarId: string;
}

export interface Address {
  street: string;
  number: string;
  postalCode: string;
  country: string;
}
