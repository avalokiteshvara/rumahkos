@inject('helper', 'App\Helpers\HelpersFunction')
@extends('pemilik.layout')
@section('content')
<!-- Content Header (Page header) -->
<section class="content-header">
   <div class="container-fluid">
      <div class="row mb-2">
         <div class="col-sm-6">
            <h1>Data Penyewa</h1>
         </div>
         <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
               <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik') }}">Home</a></li>
               <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik/rumah-kos') }}">Data Rumah Kos</a></li>
               <li class="breadcrumb-item active">Penyewa</li>
            </ol>
         </div>
      </div>
   </div>
   <!-- /.container-fluid -->
</section>
<section class="content">
   <div class="container-fluid">
      <div class="row">
         <div class="col-12">
            <div class="card">
               <div class="card-header container-fluid">
                  <div class="row">
                     <div class="col-md-10">
                        <h3 class="card-title">Data Penyewa</h3>
                     </div>
                     <div class="col-md-2">
                     <a href="{{ URL::to('/pemilik/penyewa-tambah/' . $kos_id) }}" class="btn btn-primary float-right">Tambah Penyewa</a>              
                     </div>
                  </div>
               </div>
               <!-- /.card-header -->
               <div class="card-body">
                  <table id="dataTables" class="table table-bordered table-striped">
                     <thead>
                        <tr>
                           <th>Nama Kamar</th>
                           <th>Nama Penyewa</th>
                           <th>Tanggal Sewa</th>
                           <th>Jatuh Tempo</th>
                           <th>Bulan Sewa</th>
                           <th>Harga Sewa (Total)</th>                           
                           <th>Opsi</th>
                        </tr>
                     </thead>
                     <tbody>
                        @foreach($data as $d)
                        <tr>
                           <td>{{ $d->nama_kamar }}</td>
                           <td>{!! $d->nama_penyewa !!}</td>
                           <td>{!! $d->tgl_sewa !!}</td>
                           <td>{!! $d->tgl_jatuh_tempo !!}</td>
                           <td>{!! $d->bulan_sewa !!}</td>
                           <td>{!! $helper->rupiah($d->harga_total) !!}</td>
                           
                           <td>                              
                              <a href="{!! URL::to('/pemilik/edit-sewa/' .  sha1($d->id)) !!}" class="btn btn-warning btn-xs">Edit</a>
                              <a href="{!! URL::to('/pemilik/nonaktifkan-sewa/' . sha1($d->id)) !!}" class="btn btn-danger btn-xs">Non Aktifkan !</a>
                              
                           </td>
                        </tr>
                        @endforeach
                     </tbody>
                  </table>
               </div>
               <!-- /.card-body -->
            </div>
            <!-- /.card -->
         </div>
      </div>
   </div>
</section>
<script>
   $(function () {
     
     $('#dataTables').DataTable({
       "paging": true,
       "lengthChange": false,
       "searching": true,
       "ordering": true,
       "info": true,
       "autoWidth": false,
       "responsive": true,
     });
   });
</script>
@endsection