;; Freecoin - digital social currency toolkit

;; part of Decentralized Citizen Engagement Technologies (D-CENT)
;; R&D funded by the European Commission (FP7/CAPS 610349)

;; Copyright (C) 2015 Dyne.org foundation
;; Copyright (C) 2015 Thoughtworks, Inc.

;; Sourcecode designed, written and maintained by
;; Denis Roio <jaromil@dyne.org>

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.


(ns freecoin.blockchain
  (:require
   [clojure.string :as str]

   [freecoin.fxc :as fxc]
   [freecoin.random :as rand]
   [freecoin.params :as param]
   [freecoin.storage :as storage]

   )
  )

(defprotocol Blockchain
  ;; account
  (import-account [bk wallet secret])
  (create-account [bk wallet])

  (get-account [bk wallet])
  (get-address [bk wallet])
  (get-balance [bk wallet])

  ;; transactions
  (list-transactions [bk wallet])
  (get-transaction   [bk wallet txid])
  (make-transaction  [bk wallet amount recipient secret])

  ;; vouchers
  (create-voucher [bk wallet amount expiration secret])
  (redeem-voucher [bk wallet voucher])
  )

(defrecord voucher [_id expiration sender
                    amount blockchain currency])

(defrecord transaction
    [_id emission broadcast signed sender
    amount resipient blockchain currency])


(defn recname [record]
  "Return a string which is the name of the record class, uppercase. Used to identify the class type."
  (str/upper-case (last (str/split (pr-str (class record)) #"\.")))
  )
  
;; TODO
(defrecord nxt  [server port])

;; inherits from Blockchain and implements its methods
(defrecord stub []
  Blockchain
  (import-account [bk wallet secrets] nil)

  ;; return an updated wallet map
  (create-account [bk wallet]
    {:pre [(contains? wallet :name)]}

    (if (contains? (:blockchains wallet) (keyword (recname bk)))
      {:status ::error
       :problem (str "Account already present in wallet: " (recname bk))}
      ;; else
      (let [secret (fxc/create-secret param/encryption (recname bk))
            new-bk-pub (assoc-in wallet
                                 [:blockchains (keyword (recname bk))]
                                 (:_id secret))]
        (assoc-in new-bk-pub
                  [:blockchain-secrets (keyword (recname bk))]
                  secret)
      ;; TODO: wrap all this with symmetric encryption using secrets
        ))
    )

  (get-account [bk wallet]
    (get-in wallet [:blockchains (keyword (recname bk))]))

  (get-address [bk wallet] nil)
  (get-balance [bk wallet] nil)

  (list-transactions [bk wallet] nil)
  (get-transaction   [bk wallet txid] nil)
  (make-transaction  [bk wallet amount recipient secret] nil)

  (create-voucher [bk wallet amount expiration secret] nil)
  (redeem-voucher [bk wallet voucher] nil)
  )

(defn new-stub [] (stub.))
;; example
;;  (b/create-account (b/_create "STUB" "sadsd" 444)
;;                    (w/new "csdaz" "ca@sdasd") {})


;; (defrecord account
;;     [_id public-key private-key
;;      blockchains blockchain-secrets])