import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, ScrollView, StyleSheet } from 'react-native';
import { useCart } from '../context/CartContext';

const MIN_ORDER = 10;

export default function CartScreen({ navigation }) {
  const {
    items,
    setQuantity,
    removeItem,
    subtotal,
    total,
    discountCode,
    setDiscountCode,
  } = useCart();

  const [message, setMessage] = useState('');

  function onProceed() {
    // FIXED (BUG-006): reads the live `total`, which is always current.
    if (total < MIN_ORDER) {
      setMessage(`Minimum order value is $${MIN_ORDER.toFixed(2)}`);
      return;
    }
    setMessage('');
    // FIXED (BUG-011): navigate to Checkout.
    navigation.navigate('Checkout');
  }

  if (items.length === 0) {
    return (
      <View style={styles.empty}>
        <Text testID="cart-empty" style={styles.emptyText}>
          Your cart is empty
        </Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container} contentContainerStyle={{ padding: 16 }}>
      {items.map(({ product, quantity }) => (
        <View key={product.id} testID={`cart-item-${product.id}`} style={styles.row}>
          <View style={{ flex: 1 }}>
            <Text style={styles.name}>{product.name}</Text>
            <Text style={styles.unit}>${product.price} each</Text>
            <Text testID={`line-total-${product.id}`} style={styles.line}>
              Line: ${product.price * quantity}
            </Text>
          </View>

          <View style={styles.stepper}>
            <TouchableOpacity
              testID={`qty-dec-${product.id}`}
              style={styles.stepBtn}
              onPress={() => setQuantity(product.id, quantity - 1)}
            >
              <Text style={styles.stepText}>−</Text>
            </TouchableOpacity>

            <Text testID={`qty-${product.id}`} style={styles.qty}>
              {quantity}
            </Text>

            <TouchableOpacity
              testID={`qty-inc-${product.id}`}
              style={styles.stepBtn}
              onPress={() => setQuantity(product.id, quantity + 1)}
            >
              <Text style={styles.stepText}>+</Text>
            </TouchableOpacity>
          </View>

          <TouchableOpacity testID={`remove-${product.id}`} onPress={() => removeItem(product.id)}>
            <Text style={styles.remove}>Remove</Text>
          </TouchableOpacity>
        </View>
      ))}

      <View style={styles.discountRow}>
        <TextInput
          testID="discount-input"
          accessibilityLabel="Discount code"
          style={styles.discountInput}
          placeholder="Discount code"
          autoCapitalize="characters"
          value={discountCode}
          onChangeText={setDiscountCode}
        />
        <TouchableOpacity
          testID="apply-discount"
          style={styles.applyBtn}
          onPress={() => setDiscountCode(discountCode)}
        >
          <Text style={styles.applyBtnText}>Apply</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.totals}>
        <Text style={styles.subtotal}>Subtotal: ${Number(subtotal).toFixed(2)}</Text>
        {/* FIXED (BUG-006): render the live total directly. */}
        <Text testID="order-total" style={styles.total}>
          Total: ${Number(total).toFixed(2)}
        </Text>
      </View>

      {message ? (
        <Text testID="cart-message" style={styles.message}>
          {message}
        </Text>
      ) : null}

      <TouchableOpacity testID="proceed-checkout" style={styles.proceed} onPress={onProceed}>
        <Text style={styles.proceedText}>Proceed to Checkout</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f6f6f9' },
  empty: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#f6f6f9' },
  emptyText: { fontSize: 18, color: '#666' },
  row: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', borderRadius: 12, padding: 14, marginBottom: 12 },
  name: { fontSize: 15, fontWeight: '600', color: '#1a1442' },
  unit: { fontSize: 13, color: '#888', marginTop: 2 },
  line: { fontSize: 13, color: '#3b2fb5', marginTop: 2 },
  stepper: { flexDirection: 'row', alignItems: 'center', marginHorizontal: 8 },
  stepBtn: { width: 30, height: 30, borderRadius: 6, backgroundColor: '#e6e4f5', alignItems: 'center', justifyContent: 'center' },
  stepText: { fontSize: 20, color: '#3b2fb5', fontWeight: '700' },
  qty: { minWidth: 28, textAlign: 'center', fontSize: 16, fontWeight: '700' },
  remove: { color: '#c0392b', fontSize: 13 },
  discountRow: { flexDirection: 'row', alignItems: 'center', marginTop: 4, marginBottom: 12 },
  discountInput: { flex: 1, borderWidth: 1, borderColor: '#ccc', borderRadius: 10, padding: 12, backgroundColor: '#fff' },
  applyBtn: { backgroundColor: '#3b2fb5', paddingVertical: 12, paddingHorizontal: 16, borderRadius: 10, marginLeft: 8 },
  applyBtnText: { color: '#fff', fontWeight: '700' },
  totals: { marginTop: 4 },
  subtotal: { fontSize: 15, color: '#666' },
  total: { fontSize: 20, fontWeight: '800', color: '#1a1442', marginTop: 4 },
  message: { color: '#c0392b', marginTop: 12 },
  proceed: { backgroundColor: '#3b2fb5', padding: 16, borderRadius: 10, alignItems: 'center', marginTop: 20 },
  proceedText: { color: '#fff', fontSize: 17, fontWeight: '700' },
});
