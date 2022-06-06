using System;
using System.Collections.Generic;
using api_contact.Dto;

namespace api_contact_test;

public static class TestData
{
    public static Guid UserId = Guid.Parse("4eb9ea93-c485-4154-9943-9e15d143f954");
    public static string AuthToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjRlYjllYTkzLWM0ODUtNDE1NC05OTQzLTllMTVkMTQzZjk1NCIsIm5iZiI6MTY0NDE3NzUyOSwiZXhwIjoxNjQ0NzgyM" +
                                     "zI5LCJpYXQiOjE2NDQxNzc1MjksImlzcyI6InVzZXItYXBpIiwiYXVkIjoiKi1hcGkifQ.FMHVtU4ObqfLW_AXwcH3pbURplVnzvmVNQU_pRLtYZh_A4s0Ahzpaa_QISFvo0r6cC1Nt06Y4wQAq" +
                                     "4lJ5nEYIcx0NJJAy067dVXLkIaFxUKbQ5D1FlDaBPn6twM-AgBoCrftrP3ZGxGYpo6Digc64Xlhd8IUt8-OUaJ73plBlvkZGxvVOYP8CZ4Scio5X7DsizqQ73_S7OIv110w9HYnDBTvfIwC-3TsNZs" +
                                     "CmIiMkLajwTmvmZSSVvfmZZqjycV7Lg6_TzAM1oIASP0k3JE90rK--RXmtc7DX5PnQXF9fvlEaGmQps_Ony1sE0JATBrUVTZOww2t-qcJJcwuIbRs_w";
    public static readonly List<AddressDto> AddressDtos1 = new List<AddressDto>()
    {
        new()
        {
            Country = "Deutschland", Number = "57a", PostalCode = "97999", City = "Igersheim", Street = "Waldowstr"
        },
        new()
        {
            Country = "Deutschland", Number = "83", PostalCode = "68623", City = "Lampertheim",
            Street = "Scharnweberstrasse"
        }
    };
    
    public static readonly List<AddressDto> AddressDtos2 = new List<AddressDto>()
    {
        new()
        {
            Country = "Deutschland", Number = "22", PostalCode = "61815", City = "Bornheim", Street = "Waldwiesn"
        },
        new()
        {
            Country = "Deutschland", Number = "71", PostalCode = "12572", City = "Ingolstadt", Street = "Am Damm"
        },
        new()
        {
            Country = "Deutschland", Number = "89", PostalCode = "38392", City = "Siegburg", Street = "Große Ziegelstraße"
        }
    };


    public static readonly List<NewContactDto> NewContactDtos = new List<NewContactDto>()
    {
        new()
        {
            Addresses = AddressDtos1, Birthday = "1956-05-17", Email = "DieterWerfel@einrot.com", Firstname = "Dieter",
            Lastname = "Werfel", PhoneNumber = "07931 95 31 46",
        }, 
        new ()
        {
            Addresses = AddressDtos2, Birthday = "1977-08-23", Email = "MarkusMüller@zweibein.de", Firstname = "Markus",
            Lastname = "Müller", PhoneNumber = "01768246379206",
        }
    };
}