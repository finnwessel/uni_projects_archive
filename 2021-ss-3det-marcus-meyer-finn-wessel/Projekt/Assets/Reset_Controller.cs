using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Reset_Controller : MonoBehaviour
{
    private void OnTriggerEnter(Collider other)
    {
        if (other.gameObject.name.Equals("Player")) {
            GameManager.Instance.Lost("You ended up in space");
        }
    }
}
