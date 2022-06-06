using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ElectricFieldController : MonoBehaviour
{
    private void OnTriggerStay(Collider other)
    {
        if (other.gameObject.name == "Player")
        {
            GameManager.Instance.DecLife(0.1f * Time.deltaTime);
        }
    }
}
