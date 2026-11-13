// Executar no banco da PoC antes de iniciar a entrega 2 com dados da entrega 1.
// Sem apagar documentos ou inventar eventos anteriores. Pode ser executado novamente.
const resultado = db.doacoes.updateMany(
    {status: {$exists: false}},
    {$set: {status: "DISPONIVEL", beneficiario: null, historico: [], versao: NumberLong("0")}}
);
printjson({documentosMigrados: resultado.modifiedCount});
