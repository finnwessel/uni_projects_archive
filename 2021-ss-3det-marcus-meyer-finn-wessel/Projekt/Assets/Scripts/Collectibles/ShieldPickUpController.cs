using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ShieldPickUpController : MonoBehaviour {
    private void OnTriggerEnter(Collider other) {
        GameManager.Instance.AddShield();
        Destroy(gameObject);
    }
}
